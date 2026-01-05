package it.auties.analyzer

import it.auties.whatsapp.io.BinaryDecoder
import it.auties.whatsapp.model.node.Node
import org.openqa.selenium.devtools.v143.network.model.WebSocketFrameReceived
import org.openqa.selenium.devtools.v143.network.model.WebSocketFrameSent
import java.io.ByteArrayInputStream
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.max

fun handleReceivedMessage(msg: WebSocketFrameReceived) {
    val payload = msg.response.payloadData
    if (msg.response.opcode.toInt() != 2) {
        return
    }

    handleBinaryMessage(payload, false)
}

fun handleSentMessage(msg: WebSocketFrameSent) {
    val payload = msg.response.payloadData
    if (msg.response.opcode?.toInt() != 2) {
        return
    }

    handleBinaryMessage(payload, true)
}

private fun handleBinaryMessage(payload: String, request: Boolean) {
    val message = runCatching {
        MessageWrapper(Base64.getDecoder().decode(payload))
    }.getOrNull() ?: return

    val counter = if(request) whatsappKeys.writeIv.getAndIncrement() else whatsappKeys.readIv.getAndIncrement()
    for (key in whatsappKeys.keys) {
        val result = decodeNodes(message, counter, key, request)
        if(result.isNotEmpty()){
            return
        }
    }
    println("Cannot decode node")
}

private fun decodeNodes(
    wrapper: MessageWrapper,
    counter: Long,
    key: ByteArray,
    request: Boolean
): List<Node> = wrapper.decoded
    .mapNotNull { tryDecodeNode(counter, it, key, request) }
    .toList()

private fun tryDecodeNode(
    counter: Long,
    it: ByteArray,
    key: ByteArray,
    request: Boolean
): Node? {
    val result = decodeNode(counter, it, key, request)
    if (result != null) {
        return result
    }

    // Try to rescue the message, there is obviously a better way, but it works and performs well enough
    val lowerBound = max(counter - 10, 0)
    val upperBound = counter + 10
    return (lowerBound..upperBound)
        .firstNotNullOfOrNull { hypotheticalCounter -> decodeNode(hypotheticalCounter, it, key, request) }
}

private fun decodeNode(counter: Long, decoded: ByteArray, key: ByteArray, request: Boolean): Node? = runCatching {
    val plainText = decryptAesGcm(counter, decoded, key)
    val node = BinaryDecoder.decode(ByteArrayInputStream(plainText))
    if (request) {
        onMessageSent(node)
    } else {
        onMessageReceived(node)
    }
    node
}.getOrNull()

/**
 * Custom AES-GCM decryption implementation since AesGcm class was removed in v0.0.10
 */
private fun decryptAesGcm(counter: Long, data: ByteArray, key: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val secretKey = SecretKeySpec(key, "AES")
    val iv = createGcmIv(counter)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
    return cipher.doFinal(data)
}

/**
 * Creates GCM IV from counter (same format as WhatsApp protocol)
 */
private fun createGcmIv(counter: Long): GCMParameterSpec {
    val iv = ByteArray(12)
    iv[4] = (counter shr 56).toByte()
    iv[5] = (counter shr 48).toByte()
    iv[6] = (counter shr 40).toByte()
    iv[7] = (counter shr 32).toByte()
    iv[8] = (counter shr 24).toByte()
    iv[9] = (counter shr 16).toByte()
    iv[10] = (counter shr 8).toByte()
    iv[11] = counter.toByte()
    return GCMParameterSpec(128, iv)
}
