package com.baranproject.backendmastery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Temel "Smoke Test" — Spring context'in hatasız ayağa kalkıp kalkmadığını kontrol eder.
 *
 * @SpringBootTest → Tam Spring Application Context'ini yükler.
 *                    Tüm bean'ler oluşturulur, konfigürasyon okunur.
 *
 * @ActiveProfiles("dev") → Test sırasında 'dev' profilini kullan.
 *                           Böylece application-dev.yml ayarları yüklenir.
 */
@SpringBootTest
@ActiveProfiles("dev")
class BackendMasteryApplicationTests {

    @Test
    void contextLoads() {
        // Bu test metodu boş — amacı sadece context'in hatasız yüklenmesini doğrulamak.
        // Eğer herhangi bir bean oluşturma hatası varsa, bu test FAIL olur.
    }
}
