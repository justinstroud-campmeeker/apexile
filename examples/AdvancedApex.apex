@AuraEnabled
public class AdvancedApex {
    private List<Account> accounts;
    private Map<Id, Contact> contactMap;
    
    @AuraEnabled
    public static List<Account> getAccounts() {
        List<Account> result = [SELECT Id, Name FROM Account LIMIT 10];
        return result;
    }
    
    @TestMethod
    static void testAccountRetrieval() {
        List<Account> testAccounts = getAccounts();
        System.assertEquals(2, testAccounts.size());
    }
    
    public void processAccounts() {
        accounts = [SELECT Id, Name, Industry FROM Account WHERE Industry != null];
        
        for (Account acc : accounts) {
            if (acc.Industry == 'Technology') {
                acc.Name = acc.Name + ' (Tech)';
            }
        }
        
        update accounts;
        System.debug('Updated ' + accounts.size() + ' accounts');
    }
}