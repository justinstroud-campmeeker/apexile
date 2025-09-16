public class Calculator {
    private Integer result;
    
    public Calculator() {
        result = 0;
    }
    
    public Integer add(Integer a, Integer b) {
        result = a + b;
        return result;
    }
    
    public Integer subtract(Integer a, Integer b) {
        result = a - b;
        return result;
    }
    
    public Integer multiply(Integer a, Integer b) {
        result = a * b;
        return result;
    }
    
    public Decimal divide(Integer a, Integer b) {
        if (b == 0) {
            return null;
        }
        Decimal decimalResult = a / b;
        return decimalResult;
    }
    
    public Integer getResult() {
        return result;
    }
    
    public void reset() {
        result = 0;
    }
    
    public Boolean isPositive() {
        return result > 0;
    }
}