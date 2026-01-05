package com.github.auties00.examples

import it.auties.whatsapp.api.QrHandler
import it.auties.whatsapp.api.Whatsapp

/**
 * Basit WhatsApp bot örneği - Mesaj gönderme
 *
 * KULLANIM:
 * 1. QR kodu telefonunuzla tarayın
 * 2. Birine mesaj atın
 * 3. Bot otomatik cevap verecek
 */
fun main() {
    println("\n⏳ WhatsApp'a bağlanıyor...\n")

    Whatsapp.webBuilder()
        .newConnection()
        .unregistered(QrHandler.toTerminal())
        .addLoggedInListener { api ->
            println("\n✅ Başarıyla bağlandı!")
            println("📱 Telefon: ${api.store().jid().toPhoneNumber()}")
            println("🤖 Bot hazır, mesajları dinliyor...\n")
        }
        .addDisconnectedListener { reason ->
            println("❌ Bağlantı kesildi: $reason")
        }
        .addNewChatMessageListener { whatsapp, info ->
            println("\n📩 Yeni mesaj geldi!")

            // Chat bilgisi
            val chatOpt = whatsapp.store().findChatByJid(info.chatJid())
            if (chatOpt.isPresent) {
                val chat = chatOpt.get()
                println("   💬 Chat: ${chat.name()}")
            }

            // Gönderen bilgisi
            val senderJid = info.senderJid()
            val senderOpt = whatsapp.store().findContactByJid(senderJid)
            val senderName = if (senderOpt.isPresent) {
                senderOpt.get().name()
            } else {
                senderJid.user()
            }
            println("   👤 Gönderen: $senderName")

            // Mesaj içeriği
            val messageContent = info.message().content()
            println("   💭 Mesaj: $messageContent")

            // Otomatik cevap gönder (sadece text mesajlarına)
            if (messageContent != null && messageContent.toString().isNotEmpty()) {
                val response = "Merhaba! Mesajınızı aldım: \"$messageContent\""

                println("   🤖 Cevap gönderiliyor...")
                whatsapp.sendMessage(info.chatJid(), response)
                    .thenAccept { sentInfo ->
                        println("   ✅ Cevap gönderildi! ID: ${sentInfo.id()}")
                    }
                    .exceptionally { error ->
                        println("   ❌ Hata: ${error.message}")
                        null
                    }
            }
            println()
        }
        .connect()
        .join()
        .awaitDisconnection()

    println("\n👋 Bot kapatıldı.")
}
