package myone.datajpa.etc;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptApplicationTests {
	@Test
    void jasypt() {
		String url = "jdbc:postgresql://localhost:5432/basicdb";
        String username = "dikalee";
        String password = "d09209233!";

        System.out.println(jasyptEncoding(url));
        System.out.println(jasyptEncoding(username));
        System.out.println(jasyptEncoding(password));
    }

    public String jasyptEncoding(String value) {

        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(System.getenv("JASYPT_PASSWORD"));
        return pbeEnc.encrypt(value);
    }
}
