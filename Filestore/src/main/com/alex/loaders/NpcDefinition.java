package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.store.Store;
import com.alex.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class NpcDefinition implements Cloneable {
    public HashMap<Integer, BasDefinition> basDefinition = new HashMap<>();
    public int id;
    public String name = "null";
    public int size = 1;
    public int[] modelIndices;
    public int[] headmodels;
    public short[] recol_s;
    public short[] recol_d;
    public short[] retex_s;
    public short[] retex_d;
    public byte[] recol_p;
    public String[] options = new String[5];
    public int resizeX = 128;
    public int resizeY = 128;
    public int ambient;
    public int contrast;
    public boolean hasshadow = true;
    public int headicon = -1;
    public byte loginscreenproperties = 0;
    public boolean minimapdisplay = true;
    public boolean toprenderpriority = false;
    public boolean interactive = true;
    public boolean rotationflag = true;
    public int multiNpcVarbit = -1;
    public int multiNpcVarp = -1;
    public int[] multiNpcs;
    public short shadowcolor1;
    public short shadowcolor2;
    public byte shadowcolormodifier1;
    public byte shadowcolormodifier2;
    public int[][] modeloffsets;
    public int idleSound = -1;
    public int crawlSound = -1;
    public int walkSound = -1;
    public int runSound = -1;
    public int soundRadius;
    public int cursor1Op;
    public int cursor1;
    public int cursor2Op;
    public int hitBarId = -1;
    public int cursor2;
    public int attackCursor = -1;
    public int combatLevel = 0;
    public int rotationspeed = 32;
    public byte spawndirection = 7;
    public int bastypeid = -1;
    public int iconHeight = -1;
    public int minimapmarkerobjectentry = -1;
    public Map<Integer, Object> params;

    public NpcDefinition(int id) {
        this.id = id;
    }

    public NpcDefinition(Store cache, int id) {
        this(cache, id, true);
    }

    public NpcDefinition(Store cache, int id, boolean load) {
        this.id = id;

        if (load) {
            loadNPCDefinition(cache);
        }
    }

    public static NpcDefinition getNpcDefinition(Store cache, int npcId) {
        return getNpcDefinition(cache, npcId, true);
    }

    public static NpcDefinition getNpcDefinition(Store cache, int npcId, boolean load) {
        return new NpcDefinition(cache, npcId, load);
    }

    public void loadNPCDefinition(Store cache) {
        byte[] data = cache.getIndexes()[18].getFile(this.getArchiveId(), this.getFileId());

        if (data != null) {
            try {
                this.parse(new com.alex.io.InputStream(data));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            decode(stream, opcode);
        }
    }

    public void decode(InputStream stream, int opcode) {
        switch (opcode) {
            case 1: {
                int count = stream.readUnsignedByte();
                modelIndices = new int[count];

                for (int i = 0; i < count; i++) {
                    int id = stream.readUnsignedShort();
                    modelIndices[i] = (id == 65535) ? -1 : id;
                }
                break;
            }
            case 2:
                name = stream.readString();
                break;

            case 12:
                size = stream.readUnsignedByte();
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34: {
                int idx = opcode - 30;
                String opt = stream.readString();

                if (opt.equalsIgnoreCase("Hidden")) {
                    opt = null;
                }

                options[idx] = opt;
                break;
            }
            case 40: {
                int count = stream.readUnsignedByte();
                recol_d = new short[count];
                recol_s = new short[count];

                for (int i = 0; i < count; i++) {
                    recol_s[i] = (short) stream.readUnsignedShort();
                    recol_d[i] = (short) stream.readUnsignedShort();
                }
                break;
            }
            case 41: {
                int count = stream.readUnsignedByte();
                retex_d = new short[count];
                retex_s = new short[count];

                for (int i = 0; i < count; i++) {
                    retex_s[i] = (short) stream.readUnsignedShort();
                    retex_d[i] = (short) stream.readUnsignedShort();
                }
                break;
            }
            case 42: {
                int count = stream.readUnsignedByte();
                recol_p = new byte[count];

                for (int i = 0; i < count; i++) {
                    recol_p[i] = (byte) stream.readByte();
                }
                break;
            }
            case 60: {
                int count = stream.readUnsignedByte();
                headmodels = new int[count];

                for (int i = 0; i < count; i++) {
                    headmodels[i] = stream.readUnsignedShort();
                }
                break;
            }
            case 93:
                minimapdisplay = false;
                break;
            case 95:
                combatLevel = stream.readUnsignedShort();
                break;
            case 97:
                resizeX = stream.readUnsignedShort();
                break;
            case 98:
                resizeY = stream.readUnsignedShort();
                break;
            case 99:
                toprenderpriority = true;
                break;
            case 100:
                ambient = stream.readByte();
                break;
            case 101:
                contrast = stream.readByte() * 5;
                break;
            case 102:
                headicon = stream.readUnsignedShort();
                break;
            case 103:
                rotationspeed = stream.readUnsignedShort();
                break;
            case 106:
            case 118: {
                multiNpcVarbit = stream.readUnsignedShort();
                if (multiNpcVarbit == 65535) multiNpcVarbit = -1;

                multiNpcVarp = stream.readUnsignedShort();
                if (multiNpcVarp == 65535) multiNpcVarp = -1;

                int defaultNpc = -1;

                if (opcode == 118) {
                    defaultNpc = stream.readUnsignedShort();
                    if (defaultNpc == 65535) defaultNpc = -1;
                }

                int count = stream.readUnsignedByte();
                multiNpcs = new int[count + 2];

                for (int i = 0; i <= count; i++) {
                    int id = stream.readUnsignedShort();
                    multiNpcs[i] = (id == 65535) ? -1 : id;
                }

                multiNpcs[count + 1] = defaultNpc;
                break;
            }
            case 107:
                interactive = false;
                break;
            case 109:
                rotationflag = false;
                break;
            case 111:
                hasshadow = false;
                break;
            case 113:
                shadowcolor1 = (short) stream.readUnsignedShort();
                shadowcolor2 = (short) stream.readUnsignedShort();
                break;
            case 114:
                shadowcolormodifier1 = (byte) stream.readByte();
                shadowcolormodifier2 = (byte) stream.readByte();
                break;
            case 115:
                stream.readUnsignedByte();
                stream.readUnsignedByte();
                break;
            case 119:
                loginscreenproperties = (byte) stream.readByte();
                break;
            case 121: {
                modeloffsets = new int[modelIndices.length][];
                int count = stream.readUnsignedByte();
                for (int i = 0; i < count; i++) {
                    int index = stream.readUnsignedByte();

                    int[] offsets = new int[3];
                    offsets[0] = stream.readByte();
                    offsets[1] = stream.readByte();
                    offsets[2] = stream.readByte();

                    modeloffsets[index] = offsets;
                }
                break;
            }
            case 122:
                hitBarId = stream.readUnsignedShort();
                break;
            case 123:
                iconHeight = stream.readUnsignedShort();
                break;
            case 125:
                spawndirection = (byte) stream.readByte();
                break;
            case 126:
                minimapmarkerobjectentry = stream.readUnsignedShort();
                break;
            case 127:
                bastypeid = stream.readUnsignedShort();
                break;
            case 128:
                stream.readUnsignedByte();
                break;
            case 134: {
                idleSound = stream.readUnsignedShort();
                if (idleSound == 65535) idleSound = -1;

                crawlSound = stream.readUnsignedShort();
                if (crawlSound == 65535) crawlSound = -1;

                walkSound = stream.readUnsignedShort();
                if (walkSound == 65535) walkSound = -1;

                runSound = stream.readUnsignedShort();
                if (runSound == 65535) runSound = -1;

                soundRadius = stream.readUnsignedByte();
                break;
            }
            case 135:
                cursor1Op = stream.readUnsignedByte();
                cursor1 = stream.readUnsignedShort();
                break;
            case 136:
                cursor2Op = stream.readUnsignedByte();
                cursor2 = stream.readUnsignedShort();
                break;
            case 137:
                attackCursor = stream.readUnsignedShort();
                break;
            case 249: {
                int count = stream.readUnsignedByte();

                if (params == null) {
                    params = new HashMap<>();
                }

                for (int i = 0; i < count; i++) {
                    boolean isString = stream.readUnsignedByte() == 1;
                    int key = stream.readUnsignedMedium();

                    Object value = isString ? stream.readString() : stream.readInt();

                    params.put(key, value);
                }
                break;
            }

            default:
                throw new RuntimeException("Unhandled opcode: " + opcode);
        }
    }

    public byte[] encode() {
        com.alex.io.OutputStream stream = new com.alex.io.OutputStream();

        if (modelIndices != null) {
            stream.writeByte(1);
            stream.writeByte(modelIndices.length);

            for (int id : modelIndices) {
                stream.writeShort(id == -1 ? 65535 : id);
            }
        }

        if (name != null && !name.equals("null")) {
            stream.writeByte(2);
            stream.writeString(name);
        }

        if (size != 0) {
            stream.writeByte(12);
            stream.writeByte(size);
        }

        for (int i = 0; i < 5; i++) {
            if (options == null || options[i] == null) continue;

            stream.writeByte(30 + i);
            stream.writeString(options[i]);
        }

        if (recol_s != null) {
            stream.writeByte(40);
            stream.writeByte(recol_s.length);

            for (int i = 0; i < recol_s.length; i++) {
                stream.writeShort(recol_s[i]);
                stream.writeShort(recol_d[i]);
            }
        }

        if (retex_s != null) {
            stream.writeByte(41);
            stream.writeByte(retex_s.length);

            for (int i = 0; i < retex_s.length; i++) {
                stream.writeShort(retex_s[i]);
                stream.writeShort(retex_d[i]);
            }
        }

        if (recol_p != null) {
            stream.writeByte(42);
            stream.writeByte(recol_p.length);

            for (byte b : recol_p) {
                stream.writeByte(b);
            }
        }

        if (headmodels != null) {
            stream.writeByte(60);
            stream.writeByte(headmodels.length);

            for (int id : headmodels) {
                stream.writeShort(id);
            }
        }

        if (!minimapdisplay) {
            stream.writeByte(93);
        }

        if (combatLevel != -1) {
            stream.writeByte(95);
            stream.writeShort(combatLevel);
        }

        if (resizeX != 0) {
            stream.writeByte(97);
            stream.writeShort(resizeX);
        }

        if (resizeY != 0) {
            stream.writeByte(98);
            stream.writeShort(resizeY);
        }

        if (toprenderpriority) {
            stream.writeByte(99);
        }

        if (ambient != 0) {
            stream.writeByte(100);
            stream.writeByte(ambient);
        }

        if (contrast != 0) {
            stream.writeByte(101);
            stream.writeByte(contrast / 5);
        }

        if (headicon != 0) {
            stream.writeByte(102);
            stream.writeShort(headicon);
        }

        if (rotationspeed != 0) {
            stream.writeByte(103);
            stream.writeShort(rotationspeed);
        }

        if (multiNpcVarbit != -1 || multiNpcVarp != -1) {
            stream.writeByte(118);

            stream.writeShort(multiNpcVarbit == -1 ? 65535 : multiNpcVarbit);
            stream.writeShort(multiNpcVarp == -1 ? 65535 : multiNpcVarp);

            int defaultNpc = (multiNpcs != null && multiNpcs.length > 0) ? multiNpcs[multiNpcs.length - 1] : -1;

            stream.writeShort(defaultNpc == -1 ? 65535 : defaultNpc);

            int count = multiNpcs != null ? multiNpcs.length - 2 : 0;
            stream.writeByte(count);

            for (int i = 0; i <= count; i++) {
                int id = multiNpcs[i];
                stream.writeShort(id == -1 ? 65535 : id);
            }
        }

        if (!interactive) {
            stream.writeByte(107);
        }

        if (!rotationflag) {
            stream.writeByte(109);
        }

        if (!hasshadow) {
            stream.writeByte(111);
        }

        if (shadowcolor1 != 0 || shadowcolor2 != 0) {
            stream.writeByte(113);
            stream.writeShort(shadowcolor1);
            stream.writeShort(shadowcolor2);
        }

        if (shadowcolormodifier1 != 0 || shadowcolormodifier2 != 0) {
            stream.writeByte(114);
            stream.writeByte(shadowcolormodifier1);
            stream.writeByte(shadowcolormodifier2);
        }

        if (loginscreenproperties != 0) {
            stream.writeByte(119);
            stream.writeByte(loginscreenproperties);
        }

        if (modeloffsets != null) {
            stream.writeByte(121);

            int count = 0;
            for (int[] offsets : modeloffsets) {
                if (offsets != null) count++;
            }

            stream.writeByte(count);

            for (int i = 0; i < modeloffsets.length; i++) {
                if (modeloffsets[i] == null) continue;

                stream.writeByte(i);
                stream.writeByte(modeloffsets[i][0]);
                stream.writeByte(modeloffsets[i][1]);
                stream.writeByte(modeloffsets[i][2]);
            }
        }

        if (hitBarId != 0) {
            stream.writeByte(122);
            stream.writeShort(hitBarId);
        }

        if (iconHeight != 0) {
            stream.writeByte(123);
            stream.writeShort(iconHeight);
        }

        if (spawndirection != 0) {
            stream.writeByte(125);
            stream.writeByte(spawndirection);
        }

        if (minimapmarkerobjectentry != 0) {
            stream.writeByte(126);
            stream.writeShort(minimapmarkerobjectentry);
        }

        if (bastypeid != 0) {
            stream.writeByte(127);
            stream.writeShort(bastypeid);
        }

        if (idleSound != -1 || crawlSound != -1 || walkSound != -1 || runSound != -1) {
            stream.writeByte(134);

            stream.writeShort(idleSound == -1 ? 65535 : idleSound);
            stream.writeShort(crawlSound == -1 ? 65535 : crawlSound);
            stream.writeShort(walkSound == -1 ? 65535 : walkSound);
            stream.writeShort(runSound == -1 ? 65535 : runSound);

            stream.writeByte(soundRadius);
        }

        if (cursor1 != 0) {
            stream.writeByte(135);
            stream.writeByte(cursor1Op);
            stream.writeShort(cursor1);
        }

        if (cursor2 != 0) {
            stream.writeByte(136);
            stream.writeByte(cursor2Op);
            stream.writeShort(cursor2);
        }

        if (attackCursor != 0) {
            stream.writeByte(137);
            stream.writeShort(attackCursor);
        }

        if (params != null && !params.isEmpty()) {
            stream.writeByte(249);
            stream.writeByte(params.size());

            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                Object value = entry.getValue();

                stream.writeByte(value instanceof String ? 1 : 0);
                stream.writeMedium(entry.getKey());

                if (value instanceof String) {
                    stream.writeString((String) value);
                } else {
                    stream.writeInt((Integer) value);
                }
            }
        }

        stream.writeByte(0);

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);

        return data;
    }

    public int getArchiveId() {
        return id >>> 7;
    }

    public int getFileId() {
        return id & 0x7F;
    }

    public void write(Store store) {
        store.getIndexes()[18].putFile(this.getArchiveId(), this.getFileId(), this.encode());
    }

    public static void print(Store cache, String outputFile) {
        try {
            File file = new File(outputFile);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {

                int size = Utils.getNPCDefinitionsSize(cache);

                for (int id = 0; id < size; id++) {
                    NpcDefinition npc = new NpcDefinition(cache, id, false);
                    npc.loadNPCDefinition(cache);

                    Class<?> clazz = npc.getClass();
                    Field[] fields = clazz.getDeclaredFields();

                    boolean wroteSomething = false;
                    StringBuilder buffer = new StringBuilder();

                    buffer.append("========== NPC ").append(npc.id).append(" ==========\n");

                    for (Field field : fields) {
                        if (Modifier.isStatic(field.getModifiers())) continue;

                        field.setAccessible(true);
                        Object value;
                        try {
                            value = field.get(npc);
                        } catch (Exception e) {
                            continue;
                        }

                        if (value == null) continue;
                        if (value instanceof Integer && ((Integer) value) == 0) continue;
                        if (value instanceof Integer && ((Integer) value) == -1) continue;
                        if (value instanceof Boolean && !((Boolean) value)) continue;
                        if (value instanceof String && ((String) value).isEmpty()) continue;
                        if (value instanceof int[] && ((int[]) value).length == 0) continue;
                        if (value instanceof byte[] && ((byte[]) value).length == 0) continue;
                        if (value instanceof short[] && ((short[]) value).length == 0) continue;
                        if (value instanceof Object[] && ((Object[]) value).length == 0) continue;
                        if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) continue;
                        if (value instanceof String[] && ((String[]) value).length == 0) continue;

                        String valueString;
                        if (value instanceof int[]) {
                            valueString = Arrays.toString((int[]) value);
                        } else if (value instanceof byte[]) {
                            valueString = Arrays.toString((byte[]) value);
                        } else if (value instanceof short[]) {
                            short[] arr = (short[]) value;
                            int[] unsignedArr = new int[arr.length];
                            for (int i = 0; i < arr.length; i++) {
                                unsignedArr[i] = arr[i] & 0xFFFF;
                            }
                            valueString = Arrays.toString(unsignedArr);
                        } else if (value instanceof String[]) {
                            valueString = Arrays.toString((String[]) value);
                        } else if (value instanceof Object[]) {
                            valueString = Arrays.toString((Object[]) value);
                        } else if (value instanceof Map) {
                            Map<?, ?> map = (Map<?, ?>) value;
                            StringBuilder mapStr = new StringBuilder("{");
                            for (Map.Entry<?, ?> entry : map.entrySet()) {
                                mapStr.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
                            }
                            if (!map.isEmpty()) mapStr.setLength(mapStr.length() - 2);
                            mapStr.append("}");
                            valueString = mapStr.toString();
                        } else {
                            valueString = value.toString();
                        }

                        buffer.append(field.getName()).append(" = ").append(valueString).append("\n");
                        wroteSomething = true;
                    }

                    if (wroteSomething) writer.println(buffer.toString());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            var2.printStackTrace();
            return null;
        }
    }
}