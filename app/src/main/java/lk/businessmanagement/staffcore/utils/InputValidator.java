package lk.businessmanagement.staffcore.utils;

import android.text.TextUtils;
import java.util.regex.Pattern;

public class InputValidator {

    private static final String PHONE_PATTERN = "^0\\d{9}$";

    private static final String NIC_PATTERN = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";

    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.length() > 2;
    }

    public static boolean isValidPhone(String phone) {
        return Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }

    public static boolean isValidNIC(String nic) {
        return Pattern.compile(NIC_PATTERN).matcher(nic).matches();
    }

    public static boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address);
    }
}
