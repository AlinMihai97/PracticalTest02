package ro.pub.cs.systems.eim.practicaltest02;

public class WeatherForecastInformation {
    public String getTemperature() {
        return temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public String getPressure() {
        return pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    String temperature, windSpeed, condition, pressure, humidity;

    WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String  humidity){
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
    }
}
