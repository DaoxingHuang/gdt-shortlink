package com.gdtc.deeplink.manager.utils;

import com.gdtc.deeplink.manager.core.ServiceException;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ShortCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ShortCodeGenerator.class);

    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private final static int BASE_62 = 62;
    private final static int MAX_CODE_LENGTH = 6;

    private static final int SEED = 20201210;
    private static final HashFunction MURMUR_HASH = Hashing.murmur3_32(SEED);

    public static String generateCode(String origin) {
        if (StringUtils.isBlank(origin)) {
            throw new ServiceException("invalid origin.");
        }

        HashCode hashCode = MURMUR_HASH.hashString(origin, Charset.defaultCharset());
        String code = changeBaseTo62(hashCode.asInt());
        logger.info("code: {}, origin: {}", code, origin);
        return code;
    }

    private static String changeBaseTo62(int n) {
        long num = 0;
        if (n < 0) {
            num = ((long) 2 * 0x7fffffff) + n + 2;
        } else {
            num = n;
        }
        char[] buf = new char[MAX_CODE_LENGTH];
        int charPos = MAX_CODE_LENGTH;
        while ((num / BASE_62) > 0 && charPos > 0) {
            buf[--charPos] = DIGITS[(int) (num % BASE_62)];
            num /= BASE_62;
        }

        if (num > 0 && charPos > 0) {
            buf[--charPos] = DIGITS[(int) (num % BASE_62)];
        } else if (num > 0 && charPos <= 0) {
            throw new RuntimeException("change base to 62 error, number: " + n);
        }
        return new String(buf, charPos, (MAX_CODE_LENGTH - charPos));
    }
}
