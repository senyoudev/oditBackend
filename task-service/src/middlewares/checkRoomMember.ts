import asyncHandler from "express-async-handler";
import eurekaClient from "../eureka";
import axios from "axios";

export default asyncHandler(async (req, res, next) => {
  const { userId, roomId } = req.query;
  const _roomId = req.body.roomId;

  const instance = eurekaClient.getInstancesByAppId("ROOM")[0];

  if (instance) {
    const { hostName, port } = instance;

    //data has type boolean
    const { data } = await axios.get(
      `http://${hostName}:${port.$}/api/v1/room-members/checkRoomMember?roomId=${roomId?roomId:_roomId}&userId=${userId}`
    );

    if (data) {
      next();
    } else {
      res
        .status(401)
        .send({ message: "You must be a room member to do this operation" });
    }
  } else {
    res.status(500).send("Server Error");
  }
});
