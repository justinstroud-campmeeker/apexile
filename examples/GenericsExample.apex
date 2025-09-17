public class GenericsExample {
    private List<String> names;
    private Map<Id, Account> accountMap;
    
    public void testGenerics() {
        names = new List<String>();
        accountMap = new Map<Id, Account>();
    }
}