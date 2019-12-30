package com.dev.realestatebatch.util;

public final class OpenApiContents {
    public static String APART = "apart";
    public static String OFFICETELS = "officetel";
    public static String HOUSING = "house";


    public enum OpenApiRequest{
        APART_BARGAIN("http://openapi.molit.go.kr:8081/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTrade?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s", OpenApiContents.APART, OpenApiContents.BARGAIN_NUM)
    }
}
