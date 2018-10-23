package jp.tsubakicraft.tradeshift.common.constants;

public class TsConst {

    // Trade Shift API URL
    public static String TS_GET_USER_URL = "/tradeshift/rest/external/account/info/user";
    public static String TS_GET_ALL_USER_URL = "/tradeshift/rest/external/account/%s/users";
    public static String TS_REQUEST_PURCAHSE_ORDER_URL = "/tradeshift/rest/external/documents/dispatcher";
    public static String TS_GET_TS_DOCUMENT_URL = "/tradeshift/rest/external/documents/%s";
    public static String TS_GET_TS_DOCUMENTS_URL = "/tradeshift/rest/external/documents";

    // Format
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String ISO_DATETIME_FORMAT = "yyyy-MM-ddTHH:mm:ssZ";
    public final static String HALF_SPACE = " ";
    public final static String HYPHEN = "-";
    public final static String UNDERSCORE = "_";

    public final static String ACTIVE = "1";

    public final static String NO_RE_AUTHEN = "NO_RE_AUTHEN";

}
