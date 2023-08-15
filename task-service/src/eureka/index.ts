import { Eureka } from "eureka-js-client";

const port = process.env.PORT || 5003;
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || "localhost";
const eurekaHost =
  process.env.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE || "127.0.0.1";
const ipAddr = "127.0.0.1";

export const eurekaClient: any = new Eureka({
  instance: {
    app: "Task",
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: port as number,
      "@enabled": true,
    },
    vipAddress: "Task",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn",
    },
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort as number,
    servicePath: "/eureka/apps/",
    maxRetries: 10,
    requestRetryDelay: 5000,
    //preferIpAddress: true,
  },
});

export const startEureka = async () => {
  eurekaClient.logger.level("debug");

  eurekaClient.start((error: any) => {
    console.log(error || "task service registered");
  });

  eurekaClient.on("deregistered", () => {
    console.log("after deregistered");
    process.exit();
  });
};

export default eurekaClient;
