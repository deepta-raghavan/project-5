/**
 * Basic tests for CS1501 Project 5
 * @author	Dr. Farnan
 */
package cs1501_p5;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.math.BigInteger;

import static java.time.Duration.ofSeconds;

class AllTest {
	final int DEFAULT_TIMEOUT = 190;

	HashMap<String, String[]> mult_cases;
	HashMap<String, String[]> xgcd_cases;

	@BeforeEach
	void setup_cases() {
		mult_cases = new HashMap<String, String[]>();
		mult_cases.put("4digitA", new String[] {"1834", "5849"});
		mult_cases.put("4digitB", new String[] {"8448", "5593"});

		xgcd_cases = new HashMap<String, String[]>();
		xgcd_cases.put("4digitA", new String[] {"2274", "7926"});
		xgcd_cases.put("4digitB", new String[] {"5987", "1488"});
	}

	void check_mult(String a, String b) {
		BigInteger biA = new BigInteger(a);
		BigInteger biB = new BigInteger(b);

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		BigInteger biRes = biA.multiply(biB);
		HeftyInteger hiRes = hiA.multiply(hiB);

		assertEquals(0, biRes.compareTo(new BigInteger(hiRes.getVal())));
	}

	void check_xgcd(String a, String b) {
		BigInteger biA = new BigInteger(a);
		BigInteger biB = new BigInteger(b);

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		HeftyInteger[] hiRes = hiA.XGCD(hiB);

		BigInteger biGCD = biA.gcd(biB);
		HeftyInteger hiGCD = hiRes[0];

		BigInteger x = new BigInteger(hiRes[1].getVal());
		BigInteger y = new BigInteger(hiRes[2].getVal());

		assertEquals(0, biGCD.compareTo(new BigInteger(hiGCD.getVal())));

		BigInteger biCheck = biA.multiply(x).add(biB.multiply(y));

		assertEquals(0, biGCD.compareTo(biCheck));
	}

	@ParameterizedTest(name = "Mult test {0}")
	@ValueSource(strings = {"4digitA", "4digitB"})
	void basic_mult(String c) {
		assertTimeoutPreemptively(ofSeconds(DEFAULT_TIMEOUT), () -> {
			String[] cur = mult_cases.get(c);
			check_mult(cur[0], cur[1]);
		});
	}

	@ParameterizedTest(name = "XGCD test {0}")
	@ValueSource(strings = {"4digitA", "4digitB"})
	void basic_xgcd(String c) {
		assertTimeoutPreemptively(ofSeconds(DEFAULT_TIMEOUT), () -> {
			String[] cur = xgcd_cases.get(c);
			check_xgcd(cur[0], cur[1]);
		});
	}
}
