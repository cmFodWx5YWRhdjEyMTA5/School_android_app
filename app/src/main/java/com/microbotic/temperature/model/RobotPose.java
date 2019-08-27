package com.microbotic.temperature.model;

/**
 * Copyright (c) 2019, SuZhou CsjBot. All Rights Reserved.
 * www.csjbot.com
 * <p>
 * Created by 浦耀宗 at 2019/1/8-15:41.
 * Email: puyz@csjbot.com
 */

public class RobotPose {
    /**
     * msg_id : NAVI_ROBOT_MOVE_TO_REQ
     * pos : {"x":2,"y":1,"z":0,"rotation":30}
     */

    private String msg_id;
    private String poseName;
    private PosBean pos;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public PosBean getPos() {
        return pos;
    }

    public void setPos(PosBean pos) {
        this.pos = pos;
    }

    public String getPoseName() {
        return poseName;
    }

    public void setPoseName(String poseName) {
        this.poseName = poseName;
    }

    public static class PosBean {
        /**
         * x : 2
         * y : 1
         * z : 0
         * rotation : 30
         */

        private float x;
        private float y;
        private float z;
        private float rotation;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }

        public void setZ(float z) {
            this.z = z;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }
    }
}
