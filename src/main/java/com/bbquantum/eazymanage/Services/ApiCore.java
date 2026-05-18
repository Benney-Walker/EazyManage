package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.Utitity.LocalDateAdapter;
import com.bbquantum.eazymanage.Utitity.SSLHelper;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.http.HttpClient;
import java.time.LocalDate;

public class ApiCore {

    // --- URL roots ----------------------------------------------------------
    public final String BASE_URL    = UtilityClass.BASE_URL;

    public final String FINANCE_URL = BASE_URL + "/finance";

    public final String STAFF_URL   = BASE_URL + "/staff";

    public final String STUDENT_URL = BASE_URL + "/student";

    public final String ADMIN_URL   = BASE_URL + "/admin";

    // --- API versions -------------------------------------------------------
    public final String API_VERSION_1 = "/v1";
    public final String API_VERSION_2 = "/v2";

    // --- Shared clients -----------------------------------------------------
    public final HttpClient httpClient = SSLHelper.createTrustAllHttpClient();
    public final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // --- Auth ---------------------------------------------------------------
    public final String token = SessionManager.getToken();
}
