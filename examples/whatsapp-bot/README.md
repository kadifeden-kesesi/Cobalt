# WhatsApp Bot Örnekleri

Bu klasörde Cobalt API kullanarak WhatsApp bot'ları oluşturma örnekleri bulunmaktadır.

## 📦 Dosyalar

### 1. SimpleBot.kt
Basit otomatik cevap botu.

**Özellikler:**
- ✅ Gelen tüm mesajlara otomatik cevap verir
- ✅ Mesaj bilgilerini gösterir (gönderen, chat, içerik)
- ✅ CompletableFuture ile async mesaj gönderimi

**Çalıştırma:**
```bash
mvn compile exec:java -Dexec.mainClass="com.github.auties00.examples.SimpleBotKt"
```

### 2. AdvancedBot.kt
Komut satırı arayüzü ile mesaj gönderme.

**Özellikler:**
- ✅ İsme göre kişi bulma
- ✅ Konuşmaları listeleme
- ✅ İnteraktif komut satırı
- ✅ Benzer isim önerileri

**Çalıştırma:**
```bash
mvn compile exec:java -Dexec.mainClass="com.github.auties00.examples.AdvancedBotKt"
```

**Komutlar:**
```
list                    # Konuşmaları listele
send Ahmet Merhaba!     # Mesaj gönder
quit                    # Çıkış
```

## 🚀 Hızlı Başlangıç

### 1. Adım: QR Kod ile Bağlan
```kotlin
Whatsapp.webBuilder()
    .newConnection()
    .unregistered(QrHandler.toTerminal())
    .connect()
    .join()
```

### 2. Adım: Mesaj Dinle
```kotlin
.addNewChatMessageListener { whatsapp, info ->
    println("Mesaj: ${info.message().content()}")
}
```

### 3. Adım: Mesaj Gönder
```kotlin
// İsme göre
val chat = whatsapp.store().findChatByName("Ahmet").get()
whatsapp.sendMessage(chat, "Merhaba!")

// JID'ye göre
val jid = Jid.of("905551234567@s.whatsapp.net")
whatsapp.sendMessage(jid, "Merhaba!")
```

## 📚 Mesaj Tipleri

### Text Mesajı
```kotlin
whatsapp.sendMessage(chat, "Basit text mesaj")
```

### Link ile Text
```kotlin
val message = TextMessageBuilder()
    .text("WhatsApp Web API: https://github.com/Auties00/Cobalt")
    .canonicalUrl("https://github.com/Auties00/Cobalt")
    .build()
whatsapp.sendMessage(chat, message)
```

### Resim
```kotlin
val imageBytes = Files.readAllBytes(Path.of("resim.jpg"))
val image = ImageMessageSimpleBuilder()
    .media(imageBytes)
    .caption("Güzel bir resim")
    .build()
whatsapp.sendMessage(chat, image)
```

### Konum
```kotlin
val location = LocationMessageBuilder()
    .caption("Ofisimiz burası")
    .latitude(41.0082)
    .longitude(28.9784)
    .build()
whatsapp.sendMessage(chat, location)
```

### Reaction (Emoji Tepki)
```kotlin
whatsapp.sendReaction(message, Emoji.RED_HEART)
```

## 🔍 Store API

### Konuşma Bulma
```kotlin
// İsme göre
val chat = whatsapp.store().findChatByName("Ahmet")

// JID'ye göre
val chat = whatsapp.store().findChatByJid(jid)

// Tüm konuşmalar
val allChats = whatsapp.store().chats()
```

### Kişi Bulma
```kotlin
// İsme göre
val contact = whatsapp.store().findContactByName("Ahmet")

// JID'ye göre
val contact = whatsapp.store().findContactByJid(jid)

// İsimle arama
val contacts = whatsapp.store().findContactsByName("Ahmet")
```

### Mesaj Bulma
```kotlin
val chat = whatsapp.store().findChatByName("Ahmet").get()

// Tüm mesajlar
val messages = chat.messages()

// Son mesaj
val lastMessage = chat.lastMessage()

// Yıldızlı mesajlar
val starred = chat.starredMessages()
```

## ⚙️ Gelişmiş Özellikler

### Dosya İndirme
```kotlin
.addNewChatMessageListener { whatsapp, info ->
    if (info.message().hasMedia()) {
        whatsapp.downloadMedia(info)
            .thenAccept { data ->
                Files.write(Path.of("downloaded.jpg"), data)
                println("Dosya indirildi!")
            }
    }
}
```

### Presence (Durum) Değiştirme
```kotlin
// Online/Offline
whatsapp.changePresence(true)  // Online
whatsapp.changePresence(false) // Offline

// Yazıyor...
whatsapp.changePresence(chat, ContactStatus.COMPOSING)

// Ses kaydediyor...
whatsapp.changePresence(chat, ContactStatus.RECORDING)
```

### Chat İşlemleri
```kotlin
// Okundu işaretle
whatsapp.markChatRead(chat)

// Pin yap
whatsapp.pinChat(chat)

// Arşivle
whatsapp.archiveChat(chat)

// Sil
whatsapp.deleteChat(chat)
```

### Grup İşlemleri
```kotlin
// Grup oluştur
whatsapp.createGroup("Yeni Grup", contact1, contact2)

// Katılımcı ekle
whatsapp.addGroupParticipant(group, contact)

// Admin yap
whatsapp.promoteGroupParticipant(group, contact)

// Gruptan çık
whatsapp.leaveGroup(group)
```

## 🔒 Güvenlik Notları

1. **Session Verisi**: Oturum bilgileri `$HOME/.whatsapp4j/web/<session_id>` altında saklanır
2. **QR Kod**: İlk bağlantıda QR kod taratılır, sonraki bağlantılarda gerekmez
3. **Rate Limiting**: Spam yapmayın, WhatsApp yasaklayabilir
4. **Yasal Uyarı**: Kullanıcı izni olmadan mesaj göndermeyin

## 🐛 Hata Ayıklama

### Bağlantı Hatası
```
❌ Çözüm: QR kodu tekrar taratın
whatsapp.disconnect()
// Programı yeniden başlatın
```

### "Device Removed" Hatası
```
❌ Çözüm: Telefondan "Tüm Cihazları Çıkar" tıklandı
// QR kodu tekrar taratın
```

### Mesaj Gönderilmiyor
```kotlin
// Hata yakalama ekleyin
whatsapp.sendMessage(chat, message)
    .exceptionally { error ->
        System.err.println("Hata: ${error.message}")
        error.printStackTrace()
        null
    }
```

## 📖 Daha Fazla Örnek

Ana README dosyasına bakın: `/README.md`

**Kaynaklar:**
- GitHub: https://github.com/Auties00/Cobalt
- Javadoc: https://javadoc.io/doc/com.github.auties00/cobalt/latest
- Issues: https://github.com/Auties00/Cobalt/issues

## ⚠️ Önemli

Bu bot'lar **eğitim amaçlıdır**. Production kullanımı için:
- ✅ Hata yönetimi ekleyin
- ✅ Logging ekleyin
- ✅ Rate limiting ekleyin
- ✅ Database kullanın (session management için)
- ✅ Test edin
