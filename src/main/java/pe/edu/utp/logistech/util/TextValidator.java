package pe.edu.utp.logistech.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

public final class TextValidator {

    private TextValidator() {
    }

    public static String requerido(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }
}
