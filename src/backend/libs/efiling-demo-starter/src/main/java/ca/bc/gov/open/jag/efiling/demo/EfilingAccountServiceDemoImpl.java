package ca.bc.gov.open.jag.efiling.demo;


import ca.bc.gov.open.jag.efilingcommons.model.AccountDetails;
import ca.bc.gov.open.jag.efilingcommons.service.EfilingAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EfilingAccountServiceDemoImpl implements EfilingAccountService {

    public static final UUID ACCOUNT_WITH_EFILING_ROLE = UUID.fromString("77da92db-0791-491e-8c58-1a969e67d2fa");
    public static final UUID ACCOUNT_WITHOUT_EFILING_ROLE = UUID.fromString("77da92db-0791-491e-8c58-1a969e67d2fb");
    public static final UUID ACCOUNT_DOES_NOT_EXIST = UUID.fromString("88da92db-0791-491e-8c58-1a969e67d2fb");

    private Map<UUID, AccountDetails> csoAccounts = new HashMap<>();

    public EfilingAccountServiceDemoImpl() {

        AccountDetails accountWithEfilingRole = new AccountDetails(BigDecimal.TEN, BigDecimal.TEN, true, "", "", "", "");
        AccountDetails accountWithoutEfilingRole  = new AccountDetails(BigDecimal.TEN, BigDecimal.TEN, false, "", "", "", "");
        AccountDetails accountWithoutCso  = new AccountDetails(BigDecimal.ZERO, BigDecimal.ZERO, false, "Bob", "Ross", "Rob", "bross@paintit.com");

        csoAccounts.put(ACCOUNT_WITH_EFILING_ROLE, accountWithEfilingRole);
        csoAccounts.put(ACCOUNT_WITHOUT_EFILING_ROLE, accountWithoutEfilingRole);
        csoAccounts.put(ACCOUNT_DOES_NOT_EXIST, accountWithoutCso);
    }


    public AccountDetails getAccountDetails(UUID userGuid, String bceidAccountType) {
        return csoAccounts.get(userGuid);
    }
}
