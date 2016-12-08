package com.adof.gameserver.dao.payloads.request;

/**
 * Created by mukthar on 18/11/16.
 *     //device = {"advertising_id":"675438d9-1c4b-456f-a604-d82c46a69552","build_number":"cancro",
 // "category":"Mobile","device_id":"6458b202055249a6",
 // "height":1920,"imei":"865072028292640",
 // "manufacturer":"Xiaomi","model_number":"MI 3W","os":"Android","os_api_version":19,
 // "os_version":"4.4.4","width":1080}
 */
public class Device {

    private String advId;
    private String buildNum;
    private String category;
    private String deviceId;
    private int height;
    private int width;
    private String imei;
    private String manufacturer;
    private String model_number;
    private String os;
    private String os_api_version;
    private String os_version;

    public String toString() {
        StringBuilder objDetails = new StringBuilder("Device=(");
        objDetails.append("AdvId:" + this.advId + ",");
        objDetails.append("buildNumber:" + this.buildNum + ",");
        objDetails.append("Category:" + this.category + ",");
        objDetails.append("DeviceId:" + this.deviceId + ",");
        objDetails.append("Height:" + String.valueOf(this.height) + ",");
        objDetails.append("Width:" + String.valueOf(this.width) + ",");
        objDetails.append("Imei:" + this.imei + ",");
        objDetails.append("Manufacturer:" + this.manufacturer + ",");
        objDetails.append("ModelNumber:" + this.model_number + ",");
        objDetails.append("Os:" + this.os + ",");
        objDetails.append("OsApiVer:" + this.os_api_version + ",");
        objDetails.append("OsVer:" + this.os_version + ")");

        return objDetails.toString();
    }

    public Device(String advId,
                  String buildNum,
                  String category,
                  String deviceId,
                  int height,
                  int width,
                  String imei,
                  String manufacturer,
                  String model_number,
                  String os,
                  String os_api_version,
                  String os_version) {

        this.advId = advId;
        this.buildNum = buildNum;
        this.category = category;
        this.deviceId = deviceId;
        this.height = height;
        this.width = width;
        this.imei = imei;
        this.manufacturer = manufacturer;
        this.model_number = model_number;
        this.os = os;
        this.os_version = os_version;
        this.os_api_version = os_api_version;
    }

    public String getAdvId() { return this.advId; }
    public String getBuildNum() { return this.buildNum; }
    public String getCategory() { return this.category; }
    public String getDeviceId() { return this.deviceId; }
    public int getHeight() { return this.height; }
    public int getWidth() { return this.width; }
    public String getImei() { return this.imei; }
    public String getManufacturer() { return this.manufacturer; }
    public String getModelNumber() { return this.model_number; }
    public String getOs() { return this.os; }
    public String getOsVersion() { return this.os_version; }
    public String getOsApiVersion() { return this.os_api_version; }
}
