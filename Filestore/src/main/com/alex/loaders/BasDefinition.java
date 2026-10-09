package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.io.OutputStream;
import com.alex.store.Store;
import com.alex.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BasDefinition {
    public final int id;
    public boolean isLoaded = false;
    public int[][] modelRotateTranslate;
    public int idleAnimationId = -1;
    public int walkAnimation = -1;
    public int runAnimationId = -1;
    public int slowWalkAnimationId = -1;
    public int slowWalkFullTurnAnimationId = -1;
    public int slowWalkCCWTurnAnimationId = -1;
    public int slowWalkCWTurnAnimationId = -1;
    public int runFullTurnAnimationId = -1;
    public int runCCWTurnAnimationId = -1;
    public int runCWTurnAnimationId = -1;
    public int walkFullTurnAnimationId = -1;
    public int walkCCWTurnAnimationId = -1;
    public int walkCWTurnAnimationId = -1;
    public int standingCCWTurn = -1;
    public int standingCWTurn = -1;
    public int yawAcceleration = 0;
    public int yawMaxSpeed = 0;
    public int rollAcceleration = 0;
    public int rollMaxSpeed = 0;
    public int rollTargetAngle = 0;
    public int pitchAcceleration = 0;
    public int pitchMaxSpeed = 0;
    public int pitchTargetAngle = 0;
    public int movementAcceleration = 0;
    public int hillRotateX = 0;
    public int hillRotateY = 0;

    public BasDefinition(int id) {
        this.id = id;
    }

    public BasDefinition(Store cache, int id) {
        this(cache, id, true);
    }

    public BasDefinition(Store cache, int id, boolean load) {
        this.id = id;
        this.setDefaults();

        if (load) {
            loadBasDefinition(cache);
        }
    }

    public void loadBasDefinition(Store cache) {
        try {
            byte[] data = cache.getIndexes()[2].getFile(32, this.id);

            if (data != null) {
                this.parse(new InputStream(data));
                this.isLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            decode(stream, opcode);
        }
    }

    private void setDefaults() {
        idleAnimationId = walkAnimation = slowWalkAnimationId = runAnimationId = -1;
        slowWalkFullTurnAnimationId = -1;
        slowWalkCCWTurnAnimationId = -1;
        slowWalkCWTurnAnimationId = -1;
        runFullTurnAnimationId = -1;
        runCCWTurnAnimationId = -1;
        runCWTurnAnimationId = -1;
        walkFullTurnAnimationId = -1;
        walkCCWTurnAnimationId = -1;
        walkCWTurnAnimationId = -1;
        standingCCWTurn = -1;
        standingCWTurn = -1;
        yawAcceleration = 0;
        yawMaxSpeed = 0;
        rollAcceleration = 0;
        rollMaxSpeed = 0;
        rollTargetAngle = 0;
        pitchAcceleration = 0;
        pitchMaxSpeed = 0;
        pitchTargetAngle = 0;
        movementAcceleration = 0;
    }

    private void decode(InputStream stream, int opcode) {
        switch (opcode) {
            case 1:
                idleAnimationId = stream.readUnsignedShort();
                walkAnimation = stream.readUnsignedShort();
                if (idleAnimationId == 65535) idleAnimationId = -1;
                if (walkAnimation == 65535) walkAnimation = -1;
                break;
            case 2:
                slowWalkAnimationId = stream.readUnsignedShort();
                break;
            case 3:
                slowWalkFullTurnAnimationId = stream.readUnsignedShort();
                break;
            case 4:
                slowWalkCCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 5:
                slowWalkCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 6:
                runAnimationId = stream.readUnsignedShort();
                break;
            case 7:
                runFullTurnAnimationId = stream.readUnsignedShort();
                break;
            case 8:
                runCCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 9:
                runCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 26:
                hillRotateX = stream.readUnsignedByte() * 4;
                hillRotateY = stream.readUnsignedByte() * 4;
                break;
            case 27:
                if (modelRotateTranslate == null) modelRotateTranslate = new int[12][];
                int bodyId = stream.readUnsignedByte();

                int[] arr = new int[6];
                for (int i = 0; i < 6; i++) {
                    arr[i] = stream.readSignedShort();
                }
                modelRotateTranslate[bodyId] = arr;
                break;
            case 29:
                yawAcceleration = stream.readUnsignedByte();
                break;
            case 30:
                yawMaxSpeed = stream.readUnsignedShort();
                break;
            case 31:
                rollAcceleration = stream.readUnsignedByte();
                break;
            case 32:
                rollMaxSpeed = stream.readUnsignedShort();
                break;
            case 33:
                rollTargetAngle = stream.readSignedShort();
                break;
            case 34:
                pitchAcceleration = stream.readUnsignedByte();
                break;
            case 35:
                pitchMaxSpeed = stream.readUnsignedShort();
                break;
            case 36:
                pitchTargetAngle = stream.readSignedShort();
                break;
            case 37:
                movementAcceleration = stream.readUnsignedByte();
                break;
            case 38:
                standingCCWTurn = stream.readUnsignedShort();
                break;
            case 39:
                standingCWTurn = stream.readUnsignedShort();
                break;
            case 40:
                walkFullTurnAnimationId = stream.readUnsignedShort();
                break;
            case 41:
                walkCCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 42:
                walkCWTurnAnimationId = stream.readUnsignedShort();
                break;
            case 43:
            case 44:
            case 45:
                stream.readUnsignedShort();
                break;
        }
    }

    public byte[] encode() {
        OutputStream stream = new OutputStream();

        if (idleAnimationId != -1 || walkAnimation != -1) {
            stream.writeByte(1);
            stream.writeShort(idleAnimationId == -1 ? 65535 : idleAnimationId);
            stream.writeShort(walkAnimation == -1 ? 65535 : walkAnimation);
        }

        if (slowWalkAnimationId != -1) {
            stream.writeByte(2);
            stream.writeShort(slowWalkAnimationId);
        }

        if (slowWalkFullTurnAnimationId != -1) {
            stream.writeByte(3);
            stream.writeShort(slowWalkFullTurnAnimationId);
        }

        if (slowWalkCCWTurnAnimationId != -1) {
            stream.writeByte(4);
            stream.writeShort(slowWalkCCWTurnAnimationId);
        }

        if (slowWalkCWTurnAnimationId != -1) {
            stream.writeByte(5);
            stream.writeShort(slowWalkCWTurnAnimationId);
        }

        if (runAnimationId != -1) {
            stream.writeByte(6);
            stream.writeShort(runAnimationId);
        }

        if (runFullTurnAnimationId != -1) {
            stream.writeByte(7);
            stream.writeShort(runFullTurnAnimationId);
        }

        if (runCCWTurnAnimationId != -1) {
            stream.writeByte(8);
            stream.writeShort(runCCWTurnAnimationId);
        }

        if (runCWTurnAnimationId != -1) {
            stream.writeByte(9);
            stream.writeShort(runCWTurnAnimationId);
        }

        if (hillRotateX != 0 || hillRotateY != 0) {
            stream.writeByte(26);
            stream.writeByte(hillRotateX / 4);
            stream.writeByte(hillRotateY / 4);
        }

        if (modelRotateTranslate != null) {
            for (int i = 0; i < modelRotateTranslate.length; i++) {
                int[] arr = modelRotateTranslate[i];
                if (arr != null) {
                    stream.writeByte(27);
                    stream.writeByte(i);

                    for (int val : arr) {
                        stream.writeShort(val);
                    }
                }
            }
        }

        if (yawAcceleration != 0) {
            stream.writeByte(29);
            stream.writeByte(yawAcceleration);
        }

        if (yawMaxSpeed != 0) {
            stream.writeByte(30);
            stream.writeShort(yawMaxSpeed);
        }

        if (rollAcceleration != 0) {
            stream.writeByte(31);
            stream.writeByte(rollAcceleration);
        }

        if (rollMaxSpeed != 0) {
            stream.writeByte(32);
            stream.writeShort(rollMaxSpeed);
        }

        if (rollTargetAngle != 0) {
            stream.writeByte(33);
            stream.writeShort(rollTargetAngle);
        }

        if (pitchAcceleration != 0) {
            stream.writeByte(34);
            stream.writeByte(pitchAcceleration);
        }

        if (pitchMaxSpeed != 0) {
            stream.writeByte(35);
            stream.writeShort(pitchMaxSpeed);
        }

        if (pitchTargetAngle != 0) {
            stream.writeByte(36);
            stream.writeShort(pitchTargetAngle);
        }

        if (movementAcceleration != 0) {
            stream.writeByte(37);
            stream.writeByte(movementAcceleration);
        }

        if (standingCCWTurn != -1) {
            stream.writeByte(38);
            stream.writeShort(standingCCWTurn);
        }

        if (standingCWTurn != -1) {
            stream.writeByte(39);
            stream.writeShort(standingCWTurn);
        }

        if (walkFullTurnAnimationId != -1) {
            stream.writeByte(40);
            stream.writeShort(walkFullTurnAnimationId);
        }

        if (walkCCWTurnAnimationId != -1) {
            stream.writeByte(41);
            stream.writeShort(walkCCWTurnAnimationId);
        }

        if (walkCWTurnAnimationId != -1) {
            stream.writeByte(42);
            stream.writeShort(walkCWTurnAnimationId);
        }

        stream.writeByte(0);

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);

        return data;
    }

    public static void print(Store cache, String outputFile) {
        try {
            File file = new File(outputFile);
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            if (!file.exists()) file.createNewFile();

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int size = Utils.getRenderAnimationDefinitionsSize(cache);

                for (int id = 0; id < size; id++) {

                    BasDefinition def = new BasDefinition(cache, id, false);
                    def.loadBasDefinition(cache);

                    if (!def.isLoaded) continue;

                    writer.println("========== BAS " + id + " ==========");

                    for (Field field : def.getClass().getDeclaredFields()) {
                        if (Modifier.isStatic(field.getModifiers())) continue;

                        field.setAccessible(true);

                        Object value;
                        try {
                            value = field.get(def);
                        } catch (Exception e) {
                            continue;
                        }

                        if (value == null) continue;
                        if (value instanceof Number) {
                            long num = ((Number) value).longValue();
                            if (num == 0 || num == -1) continue;
                        }

                        writer.println(field.getName() + " = " + value);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}