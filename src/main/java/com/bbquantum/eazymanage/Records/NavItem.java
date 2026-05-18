package com.bbquantum.eazymanage.Records;

import com.bbquantum.eazymanage.Constants.NavItemStatus;

import java.util.Set;

public record NavItem(String id,
                      String label,
                      String icon,
                      Set<String> roles,
                      NavItemStatus status,
                      String phase) {

    public boolean availableFor(String role) {
        return roles.contains(role);
    }
}
