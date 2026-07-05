package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.DTO.Academics.Results.StudentResult;
import com.bbquantum.eazymanage.DTO.System.FindStaffResponse;

public class ApiContext {

    private static final ApiContext INSTANCE = new ApiContext();

    public static ApiContext getInstance() { return INSTANCE; }

    private ApiContext() {}

    /** Backend response message captured by a failed service call. */
    public String errorMessageContainer = "";

    /** Backend response message captured by a successful service call. */
    public String successMessageContainer = "";

    /** Most-recently-loaded staff lookup result. Read by controllers in the
     *  staff-update flow. */
    public FindStaffResponse findStaffResponse;

    /** Most-recently-loaded per-student result set. Read by controllers
     *  rendering the results screen. */
    public StudentResult studentResult;

    public volatile boolean sessionExpired = false;
}
