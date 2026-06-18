package com.doan.VitaStore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
public class VNPAYConfig {
    private String tmnCode;
    private String hashSecret;
    private String payUrl;
    private String returnUrl;
    private String ipnUrl;
    private String version;
    private String command;

    public String getTmnCode() { return tmnCode; }
    public void setTmnCode(String tmnCode) { this.tmnCode = tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public void setHashSecret(String hashSecret) { this.hashSecret = hashSecret; }
    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }
    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }
    public String getIpnUrl() { return ipnUrl; }
    public void setIpnUrl(String ipnUrl) { this.ipnUrl = ipnUrl; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
}
