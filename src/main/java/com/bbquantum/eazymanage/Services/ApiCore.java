package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.Utitity.LocalDateAdapter;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class ApiCore {

    // --- URL roots ----------------------------------------------------------
    public final String BASE_URL    = UtilityClass.BASE_URL;

    public final String FINANCE_URL = BASE_URL + "/finance";

    public final String STAFF_URL   = BASE_URL + "/staff";

    public final String STUDENT_URL = BASE_URL + "/student";

    public final String ADMIN_URL   = BASE_URL + "/admin";

    public final String OFFLINE_URL = BASE_URL + "/offline";

    // --- API versions -------------------------------------------------------
    public final String API_VERSION_1 = "/v1";
    public final String API_VERSION_2 = "/v2";

    public final String API_VERSION_3 = "/v3";

    // --- Shared clients -----------------------------------------------------
    public final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    public final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // --- Auth ---------------------------------------------------------------
    public final String token = SessionManager.getToken();

    /*
    Apply this method too to catch all 404 and 500 responses
    and update all backend responses to parse "message"
     */
    public void checkApiResponse(HttpResponse<String> response) {
        if (response.statusCode() >= 400 && response.statusCode() <= 500) {
            JsonObject jsonObject =
                    JsonParser.parseString(response.body()).getAsJsonObject();

            ApiContext.getInstance().errorMessageContainer = jsonObject.get("message").getAsString();
        } else {
            ApiContext.getInstance().errorMessageContainer = response.body();
        }
    }

    /*
    Apply this method in all api calls to catch 401 status codes
    like this if (core.handleSessionExpired(response)) return null;
    */
    public boolean handleSessionExpired(HttpResponse<?> response) {
        if (response != null && response.statusCode() == 401) {
            ApiContext.getInstance().sessionExpired = true;
            ApiContext.getInstance().errorMessageContainer = "Your session has expired. Please log in again.";
            return true;
        }
        return false;
    }
}
