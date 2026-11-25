package com.example.mind_set.data.repository

/**
 * Koleksi pesan penutup CBT empatik yang digunakan sebagai fallback
 * ketika AI API belum tersedia (offline mode).
 */
object CbtClosingMessages {

    private val closingMessages = listOf(
        "Kamu sudah sangat berani dengan memilih berhenti dan merefleksikan perasaanmu. " +
                "Langkah ini — betapapun kecilnya — adalah bukti nyata bahwa kamu lebih kuat dari dorongan sesaat. " +
                "Perasaan berat ini tidak akan bertahan selamanya; ia akan mereda, " +
                "dan kamu akan bangun besok dengan satu hari baru yang utuh. " +
                "Kamu layak mendapatkan kedamaian itu. 💜",

        "Terima kasih sudah jujur dengan dirimu sendiri hari ini. " +
                "Menuliskan apa yang kamu rasakan butuh keberanian yang luar biasa. " +
                "Ingat: setiap kali kamu memilih jalan ini alih-alih menyerah pada dorongan, " +
                "otakmu sedang membangun jalur baru yang lebih sehat. " +
                "Kamu tidak sendirian dalam perjalanan ini. 🌱",

        "Dorongan yang kamu rasakan tadi adalah sinyal, bukan perintah. " +
                "Dengan menuliskan ceritamu, kamu sudah mengambil kendali dari pikiran otomatis itu. " +
                "Setiap gelombang yang berhasil kamu lewati membuat gelombang berikutnya sedikit lebih mudah. " +
                "Istirahatlah dengan tenang — kamu sudah melakukan hal yang benar hari ini. 🌙",

        "Kamu telah melewati momen yang berat, dan kamu memilih untuk tidak menyerah. " +
                "Itu bukan hal kecil. Refleksimu menunjukkan kesadaran diri yang dalam — " +
                "kemampuan yang akan terus membantumu di hari-hari mendatang. " +
                "Besok adalah kesempatan baru, dan kamu sudah siap menghadapinya. ✨",

        "Perasaan yang kamu ceritakan itu valid dan manusiawi. " +
                "Tidak ada yang salah dengan merasakan dorongan — yang penting adalah apa yang kamu " +
                "pilih untuk dilakukan dengannya. Dan hari ini, kamu memilih bertahan. " +
                "Setiap pilihan seperti ini memperkuat dirimu. Istirahatlah, kamu pantas mendapatkannya. 🤍"
    )

    /**
     * Menghasilkan pesan penutup CBT empatik secara acak.
     */
    fun getRandomClosingMessage(): String {
        return closingMessages.random()
    }
}
