package doorlock;

public class Door {

    private String name;
    private String currentStatus;
    private String expectedStatus;
    private String result;

    public Door(String name, String currentStatus) {
        this.name           = name;
        this.currentStatus  = currentStatus;
        this.expectedStatus = currentStatus;
        this.result         = "---";
    }

    public void checkStatus() {
        if (currentStatus.equals(expectedStatus)) {
            result = "Safe";
        } else {
            result = "Issue";
        }
    }

    public void reset(String defaultStatus) {
        currentStatus  = defaultStatus;
        expectedStatus = defaultStatus;
        result         = "---";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String status) {
        currentStatus = status;
    }

    public String getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(String status) {
        expectedStatus = status;
    }

    public String getResult() {
        return result;
    }
}
