import java.util.*;

public class MockDataService {
    private static Map<String, List<Map<String, Object>>> mockData = new HashMap<>();
    
    static {
        // Initialize with some mock data
        List<Map<String, Object>> accounts = new ArrayList<>();
        Map<String, Object> account1 = new HashMap<>();
        account1.put("Id", "001000000000001");
        account1.put("Name", "Test Account 1");
        accounts.add(account1);
        
        Map<String, Object> account2 = new HashMap<>();
        account2.put("Id", "001000000000002"); 
        account2.put("Name", "Test Account 2");
        accounts.add(account2);
        
        mockData.put("Account", accounts);
    }
    
    public static List<Map<String, Object>> executeSoql(String query) {
        System.out.println("Executing SOQL: " + query);
        
        String objectType = extractObjectType(query);
        return mockData.getOrDefault(objectType, new ArrayList<>());
    }
    
    public static void insertRecords(List<Map<String, Object>> records) {
        System.out.println("Inserting " + records.size() + " records");
        for (Map<String, Object> record : records) {
            if (!record.containsKey("Id")) {
                record.put("Id", generateId());
            }
        }
    }
    
    public static void updateRecords(List<Map<String, Object>> records) {
        System.out.println("Updating " + records.size() + " records");
    }
    
    public static void deleteRecords(List<String> ids) {
        System.out.println("Deleting " + ids.size() + " records");
    }
    
    private static String extractObjectType(String query) {
        String[] parts = query.toUpperCase().split("\\s+");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("FROM".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return "Unknown";
    }
    
    private static String generateId() {
        return String.format("%015d", new Random().nextLong() & Long.MAX_VALUE);
    }
}