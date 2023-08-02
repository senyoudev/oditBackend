import { Eureka } from "eureka-js-client";

const port = process.env.PORT || 4001;
const eurekaPort = process.env.EUREKA_PORT || 8761;
const hostName = process.env.HOSTNAME || "localhost";
const eurekaHost =
  process.env.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE || "127.0.0.1";
const ipAddr = "127.0.0.1";

const eurekaClient: any = new Eureka({
  instance: {
    app: "Feedback",
    hostName: hostName,
    ipAddr: ipAddr,
    port: {
      $: port as number,
      "@enabled": true,
    },
    vipAddress: "Feedback",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn",
    },
  },
  eureka: {
    host: eurekaHost,
    port: eurekaPort as number,
    servicePath: "/eureka/apps/",
    maxRetries: 1,
  },
});

export const startEureka = async () => {
  eurekaClient.logger.level("debug");

  eurekaClient.start((error: any) => {
    console.log(error || "feedback service registered");
  });

  eurekaClient.on("deregistered", () => {
    console.log("after deregistered");
    process.exit();
  });
};

export default eurekaClient;
