package ca.bc.gov.open.jag.efilingaccountclient;

import brooks.roleregistry_source_roleregistry_ws_provider.roleregistry.RegisteredRole;
import brooks.roleregistry_source_roleregistry_ws_provider.roleregistry.RoleRegistryPortType;
import brooks.roleregistry_source_roleregistry_ws_provider.roleregistry.UserRoles;
import ca.bc.gov.ag.csows.accounts.AccountFacadeBean;
import ca.bc.gov.ag.csows.accounts.ClientProfile;
import ca.bc.gov.ag.csows.accounts.NestedEjbException_Exception;
import ca.bc.gov.open.jag.efilingaccountclient.mappers.AccountDetailsMapper;
import ca.bc.gov.open.jag.efilingcommons.exceptions.CSOHasMultipleAccountException;
import ca.bc.gov.open.jag.efilingcommons.exceptions.EfilingAccountServiceException;
import ca.bc.gov.open.jag.efilingcommons.model.AccountDetails;
import ca.bc.gov.open.jag.efilingcommons.service.EfilingAccountService;
import ca.bceid.webservices.client.v9.*;

import java.util.*;

public class CsoAccountServiceImpl implements EfilingAccountService {

    private final AccountFacadeBean accountFacadeBean;
    private final RoleRegistryPortType roleRegistryPortType;
    private final BCeIDServiceSoap bCeIDServiceSoap;
    private final AccountDetailsMapper accountDetailsMapper;

    private static final Map<String, BCeIDAccountTypeCode> accountTypeLookup;
    static {
        Map<String, BCeIDAccountTypeCode> tempMap = new HashMap<String, BCeIDAccountTypeCode>();
        tempMap.put("business", BCeIDAccountTypeCode.BUSINESS);
        tempMap.put("individual", BCeIDAccountTypeCode.INDIVIDUAL);
        tempMap.put("verified individual", BCeIDAccountTypeCode.VERIFIED_INDIVIDUAL);
        tempMap.put("eds", BCeIDAccountTypeCode.EDS);
        tempMap.put("internal", BCeIDAccountTypeCode.INTERNAL);
        tempMap.put("ldb", BCeIDAccountTypeCode.LDB);
        tempMap.put("ths", BCeIDAccountTypeCode.THS);

        accountTypeLookup = Collections.unmodifiableMap(tempMap);;
    }

    public CsoAccountServiceImpl(AccountFacadeBean accountFacadeBean,
                                 RoleRegistryPortType roleRegistryPortType,
                                 BCeIDServiceSoap bCeIDServiceSoap,
                                 AccountDetailsMapper accountDetailsMapper) {

        this.accountFacadeBean = accountFacadeBean;
        this.roleRegistryPortType = roleRegistryPortType;
        this.bCeIDServiceSoap = bCeIDServiceSoap;
        this.accountDetailsMapper = accountDetailsMapper;
    }

    @Override
    public AccountDetails getAccountDetails(UUID userGuid, String bceidAccountType) {

        AccountDetails accountDetails = getCsoDetails(CsoHelpers.formatUserGuid(userGuid));
        if (null == accountDetails) {
            accountDetails = getBCeIDDetails(CsoHelpers.formatUserGuid(userGuid), bceidAccountType);
        }

        return accountDetails;
    }

    private AccountDetails getCsoDetails(String userGuid)  {

        AccountDetails accountDetails = null;
        List<ClientProfile> profiles = new ArrayList<>();
        try {
            profiles.addAll(accountFacadeBean.findProfiles(userGuid));
        } catch (NestedEjbException_Exception e) {
            throw new EfilingAccountServiceException("Exception while fetching account details", e);
        }
        // An account must have only one profile associated with it to proceed
        if (profiles.size() == 1) {
            accountDetails = accountDetailsMapper.toAccountDetails(profiles.get(0), hasFileRole(userGuid));
        }
        else if (profiles.size() > 1) {
            throw new CSOHasMultipleAccountException(profiles.get(0).getClientId().toString());
        }

        return accountDetails;
    }

    private AccountDetails getBCeIDDetails(String userGuid, String accountType) {

        AccountDetails accountDetails = null;
        BCeIDAccountTypeCode accountTypeCode = getBCeIDAccountType(accountType);

        if (accountTypeCode != BCeIDAccountTypeCode.VOID) {

            AccountDetailRequest request = new AccountDetailRequest();
            request.setOnlineServiceId("62B2-5550-4376-4DA7");
            request.setRequesterUserGuid(userGuid);
            request.setRequesterAccountTypeCode(accountTypeCode);
            request.setUserGuid(userGuid);
            request.setAccountTypeCode(accountTypeCode);
            AccountDetailResponse response = bCeIDServiceSoap.getAccountDetail(request);

            if (response.getCode() == ResponseCode.SUCCESS) {
                accountDetails = accountDetailsMapper.toAccountDetails(response.getAccount());
            }
        }

        return accountDetails;
    }

    public boolean hasFileRole(String userGuid) {

        UserRoles userRoles = roleRegistryPortType.getRolesForIdentifier("Courts", "CSO", userGuid, "CAP");
        List<RegisteredRole> roles = userRoles.getRoles();
        return roles != null && roles.stream().anyMatch(r -> r.getCode().equals("FILE"));
    }

    private BCeIDAccountTypeCode getBCeIDAccountType(String bceidAccountType) {
        String lookUp = bceidAccountType.toLowerCase();
        BCeIDAccountTypeCode code = accountTypeLookup.get(lookUp);
        return code == null? BCeIDAccountTypeCode.VOID : code;
    }


}
