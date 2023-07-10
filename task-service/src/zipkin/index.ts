import { Tracer, ExplicitContext, BatchRecorder, jsonEncoder } from "zipkin";
import {HttpLogger} from "zipkin-transport-http";
import {expressMiddleware} from "zipkin-instrumentation-express";
import dotenv from "dotenv";
dotenv.config();

const ZIPKIN_ENDPOINT = process.env.ZIPKIN_ENDPOINT || "http://localhost:9411";

// Get ourselves a zipkin tracer
const tracer = new Tracer({
  ctxImpl: new ExplicitContext(),
  recorder: new BatchRecorder({
    logger: new HttpLogger({
      endpoint: `${ZIPKIN_ENDPOINT}/api/v2/spans`,
      jsonEncoder: jsonEncoder.JSON_V2,
    }),
  }),
  localServiceName: "task",
});

export default expressMiddleware({ tracer });