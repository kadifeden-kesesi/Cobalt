# Cobalt Examples

Bu dizinde Cobalt WhatsApp API kullanım örnekleri bulunmaktadır.

## 📁 Dizinler

### [whatsapp-bot/](whatsapp-bot/)
WhatsApp bot örnekleri - mesaj gönderme, alma, otomatik cevap

**İçerik:**
- `SimpleBot.kt` - Basit otomatik cevap botu
- `AdvancedBot.kt` - CLI ile mesaj gönderme
- Detaylı README ve API rehberi

**Kullanım:**
```bash
cd whatsapp-bot
mvn compile exec:java -Dexec.mainClass="com.github.auties00.examples.SimpleBotKt"
```

---

## 🔍 CobaltAnalyzer Nedir?

CobaltAnalyzer (`../CobaltAnalyzer/`) bot değil, **protokol analiz aracı**dır.

| Özellik | CobaltAnalyzer | Bot Örnekleri |
|---------|----------------|---------------|
| **Amaç** | WhatsApp protokolünü analiz etmek | Mesaj göndermek/almak |
| **Hedef Kitle** | Cobalt geliştiricileri | Bot yapanlar |
| **Çıktı** | Binary Node yapıları | Mesaj içerikleri |
| **Mesaj Decode** | Sadece transport layer | Tam decode (Signal Protocol) |
| **Kullanım** | Yeni feature araştırma | Production bot |

**CobaltAnalyzer Kullanımı:**
```bash
cd ../CobaltAnalyzer
mvn compile exec:java
```

---

## 📚 Daha Fazla Örnek

Ana README: [../README.md](../README.md)

- Text mesajlar
- Medya (resim, video, ses)
- Lokasyon paylaşımı
- Grup yönetimi
- Newsletter/Channel işlemleri
- Community özellikleri

## 🔗 Kaynaklar

- **GitHub**: https://github.com/Auties00/Cobalt
- **Javadoc**: https://javadoc.io/doc/com.github.auties00/cobalt/latest
- **Maven**: https://central.sonatype.com/artifact/com.github.auties00/cobalt
