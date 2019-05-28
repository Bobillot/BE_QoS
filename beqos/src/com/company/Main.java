package com.company;

import dataStructs.Site;
import utils.ipAddrConverter;

public class Main {

    public static void main(String[] args) {
        Site site1 = new Site(ipAddrConverter.ipStringToInteger("255.255.255.0"), ipAddrConverter.ipStringToInteger("192.168.10.1"),3000);
        Site site2 = new Site(ipAddrConverter.ipStringToInteger("255.255.255.0"), ipAddrConverter.ipStringToInteger("192.168.20.1"),3000);
        BandwidthBroker BB = BandwidthBroker.getInstance();

        BB.addSite(site1,1);
        BB.addSite(site2,2);

        System.out.println("Done");
    }
}
