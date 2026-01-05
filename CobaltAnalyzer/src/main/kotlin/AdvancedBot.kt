package it.auties.analyzer

import it.auties.whatsapp.api.QrHandler
import it.auties.whatsapp.api.Whatsapp
import it.auties.whatsapp.model.message.standard.TextMessage
import java.util.Scanner

/**
 * Gelişmiş WhatsApp bot örneği - İsme göre mesaj gönderme
 *
 * Özellikler:
 * - İsme veya numaraya göre kişi bulma
 * - Farklı mesaj tipleri gönderme
 * - Komut satırından mesaj gönderme
 */
fun main() {
    val scanner = Scanner(System.`in`)

    println("\n⏳ WhatsApp'a bağlanıyor...\n")

    val whatsapp = Whatsapp.webBuilder()
        .newConnection()
        .unregistered(QrHandler.toTerminal())
        .addLoggedInListener { api ->
            println("\n✅ Başarıyla bağlandı!")
            println("📱 Telefon: ${api.store().jid().toPhoneNumber()}")
            println("\n📝 Kullanım:")
            println("   1. 'list' - Tüm konuşmaları listele")
            println("   2. 'send <isim> <mesaj>' - Mesaj gönder")
            println("   3. 'quit' - Çıkış")
            println()
        }
        .addDisconnectedListener { reason ->
            println("❌ Bağlantı kesildi: $reason")
        }
        .addNewChatMessageListener { api, info ->
            val senderJid = info.senderJid()
            val senderOpt = api.store().findContactByJid(senderJid)
            val senderName = if (senderOpt.isPresent) senderOpt.get().name() else senderJid.user()

            println("📩 ${senderName}: ${info.message().content()}")
        }
        .connect()
        .join()

    println("\n🤖 Bot hazır! Komut girin:\n")

    // Komut döngüsü
    while (true) {
        print("> ")
        val input = scanner.nextLine().trim()

        when {
            input == "quit" -> {
                println("👋 Çıkış yapılıyor...")
                whatsapp.disconnect()
                break
            }

            input == "list" -> {
                println("\n💬 Konuşmalar:")
                whatsapp.store().chats()
                    .sortedByDescending { it.timestampInSeconds() }
                    .take(10)
                    .forEachIndexed { index, chat ->
                        val unread = if (chat.unreadMessagesCount() > 0) " (${chat.unreadMessagesCount()} okunmamış)" else ""
                        println("   ${index + 1}. ${chat.name()}$unread")
                    }
                println()
            }

            input.startsWith("send ") -> {
                val parts = input.substring(5).split(" ", limit = 2)
                if (parts.size < 2) {
                    println("❌ Kullanım: send <isim> <mesaj>")
                    continue
                }

                val name = parts[0]
                val message = parts[1]

                // İsme göre chat bul
                val chatOpt = whatsapp.store().findChatByName(name)
                if (chatOpt.isEmpty) {
                    println("❌ '$name' adında konuşma bulunamadı")

                    // Benzer isimleri öner
                    val suggestions = whatsapp.store().chats()
                        .filter { it.name().contains(name, ignoreCase = true) }
                        .take(5)

                    if (suggestions.isNotEmpty()) {
                        println("   Benzer isimler:")
                        suggestions.forEach { println("   - ${it.name()}") }
                    }
                    continue
                }

                val chat = chatOpt.get()
                println("📤 Mesaj gönderiliyor: ${chat.name()}")

                whatsapp.sendMessage(chat, message)
                    .thenAccept { sentInfo ->
                        println("✅ Mesaj gönderildi! ID: ${sentInfo.id()}")
                    }
                    .exceptionally { error ->
                        println("❌ Hata: ${error.message}")
                        null
                    }
            }

            else -> {
                println("❌ Bilinmeyen komut: $input")
                println("   'list', 'send <isim> <mesaj>' veya 'quit' yazın")
            }
        }
    }

    println("👋 Bot kapatıldı.")
}
