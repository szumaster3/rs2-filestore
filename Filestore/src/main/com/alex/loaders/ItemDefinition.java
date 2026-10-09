package com.alex.loaders;

import com.alex.io.InputStream;
import com.alex.store.Store;
import com.alex.utils.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemDefinition implements Cloneable {
   public int id;
   public boolean loaded;
   public int model;
   public String name;
   public int zoom2d;
   public int xAngle2D;
   public int yAngle2D;
   public int xOffset2D;
   public int yOffset2D;
   public int equipSlot;
   public int equipType;
   public int stackable;
   public int cost;
   public boolean members;
   public int manwear;
   public int womanwear;
   public int manwear2;
   public int womanwear2;
   public int manwear3;
   public int womanwear3;
   public String[] ops;
   public String[] iops;
   public int[] recol_d;
   public int[] recol_s;
   public short[] retex_d;
   public short[] retex_s;
   public byte[] recol_p;
   public boolean stockMarket;
   public int manhead;
   public int womanhead;
   public int manhead2;
   public int womanhead2;
   public int zAngle2D;
   public int dummyItem;
   public int certlink;
   public int certtemplate;
   public int[] countobj;
   public int[] countco;
   public int resizeX;
   public int resizeY;
   public int resizeZ;
   public int ambient;
   public int contrast;
   public int team;
   public int lentLink;
   public int lentTemplate;
   public int manWearXOff;
   public int manWearYOff;
   public int manWearZOff;
   public int womanWearXOff;
   public int womanWearYOff;
   public int womanWearZOff;
   public int cursor1Op;
   public int cursor1;
   public int cursor2Op;
   public int cursor2;
   public HashMap params;

   public static ItemDefinition getItemDefinition(Store cache, int itemId) {
      return getItemDefinition(cache, itemId, true);
   }

   public static ItemDefinition getItemDefinition(Store cache, int itemId, boolean load) {
      return new ItemDefinition(cache, itemId, load);
   }

   public ItemDefinition(Store cache, int id) {
      this(cache, id, true);
   }

   public ItemDefinition(Store cache, int id, boolean load) {
      this.id = id;
      this.setDefinition();
      this.setDefaultOptions();
      if(load) {
         this.loadItemDefinition(cache);
      }
   }

   private void loadItemDefinition(Store cache) {
      byte[] data = cache.getIndexes()[19].getFile(this.getArchiveId(), this.getFileId());
      if(data != null) {
         try {
            this.readOpcodeValues(new com.alex.io.InputStream(data));
         } catch (RuntimeException var4) {
            var4.printStackTrace();
         }

         if(this.certtemplate != -1) {
            this.generateCertificate(cache);
         }

         if(this.lentTemplate != -1) {
            this.generateLent(cache);
         }

         this.loaded = true;
      }
   }

   private void generateCertificate(Store store) {
      ItemDefinition realItem = getItemDefinition(store, this.certlink);
      this.members = realItem.members;
      this.cost = realItem.cost;
      this.name = realItem.name;
      this.stackable = 1;
   }

   private void generateLent(Store store) {
      ItemDefinition link = getItemDefinition(store, this.lentLink);
      this.recol_d = link.recol_d;
      this.recol_s = link.recol_s;
      this.team = link.team;
      this.cost = 0;
      this.members = link.members;
      this.name = link.name;
      this.iops = new String[5];
      this.ops = link.ops;
      if(link.iops != null) {
         System.arraycopy(link.iops, 0, this.iops, 0, 4);
      }
      this.iops[4] = "Discard";
      this.manwear = link.manwear;
      this.manwear2 = link.manwear2;
      this.womanwear = link.womanwear;
      this.womanwear2 = link.womanwear2;
      this.manwear3 = link.manwear3;
      this.womanwear3 = link.womanwear3;
      this.equipType = link.equipType;
      this.equipSlot = link.equipSlot;
   }

   public int getArchiveId() {
      return this.id >>> 8;
   }

   public int getFileId() {
      return 0xff & this.id;
   }

    private void setDefinition() {
      this.name = "null";
      this.manwear = -1;
      this.manwear2 = -1;
      this.womanwear = -1;
      this.womanwear2 = -1;
      this.zoom2d = 2000;
      this.lentLink = -1;
      this.lentTemplate = -1;
      this.certlink = -1;
      this.certtemplate = -1;
      this.resizeZ = 128;
      this.resizeX = 128;
      this.resizeY = 128;
      this.cost = 1;
      this.manwear3 = -1;
      this.womanwear3 = -1;
      this.team = -1;
      this.equipType = -1;
      this.equipSlot = -1;
      this.manhead = -1;
      this.manhead2 = -1;
      this.womanhead = -1;
      this.womanhead2 = -1;
      this.zAngle2D = 0;
   }

    private void decode(com.alex.io.InputStream stream, int opcode) {
        switch (opcode) {
            case 1:
                this.model = stream.readUnsignedShort();
                break;
            case 2:
                this.name = stream.readString();
                break;
            case 4:
                this.zoom2d = stream.readUnsignedShort();
                break;
            case 5:
                this.xAngle2D = stream.readUnsignedShort();
                break;
            case 6:
                this.yAngle2D = stream.readUnsignedShort();
                break;
            case 7:
                this.xOffset2D = (short) stream.readUnsignedShort();
                break;
            case 8:
                this.yOffset2D = (short) stream.readUnsignedShort();
                break;
            case 11:
                this.stackable = 1;
                break;
            case 12:
                this.cost = stream.readInt();
                break;
            case 16:
                this.members = true;
                break;
            case 23:
                this.manwear = stream.readUnsignedShort();
                break;
            case 24:
                this.manwear2 = stream.readUnsignedShort();
                break;
            case 25:
                this.womanwear = stream.readUnsignedShort();
                break;
            case 26:
                this.womanwear2 = stream.readUnsignedShort();
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                this.ops[opcode - 30] = stream.readString();
                break;
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
                this.iops[opcode - 35] = stream.readString();
                break;
            case 40:
                int len40 = stream.readUnsignedByte();
                this.recol_d = new int[len40];
                this.recol_s = new int[len40];

                for (int i = 0; i < len40; i++) {
                    this.recol_d[i] = stream.readUnsignedShort();
                    this.recol_s[i] = stream.readUnsignedShort();
                }
                break;
            case 41:
                int len41 = stream.readUnsignedByte();
                this.retex_d = new short[len41];
                this.retex_s = new short[len41];

                for (int i = 0; i < len41; i++) {
                    this.retex_d[i] = (short) stream.readUnsignedShort();
                    this.retex_s[i] = (short) stream.readUnsignedShort();
                }
                break;
            case 42:
                int len42 = stream.readUnsignedByte();
                this.recol_p = new byte[len42];

                for (int i = 0; i < len42; i++) {
                    this.recol_p[i] = (byte) stream.readByte();
                }
                break;
            case 65:
                this.stockMarket = true;
                break;
            case 78:
                this.manwear3 = stream.readUnsignedShort();
                break;
            case 79:
                this.womanwear3 = stream.readUnsignedShort();
                break;
            case 90:
                this.manhead = stream.readUnsignedShort();
                break;
            case 91:
                this.womanhead = stream.readUnsignedShort();
                break;
            case 92:
                this.manhead2 = stream.readUnsignedShort();
                break;
            case 93:
                this.womanhead2 = stream.readUnsignedShort();
                break;
            case 95:
                this.zAngle2D = stream.readUnsignedShort();
                break;
            case 96:
                this.dummyItem = stream.readUnsignedByte();
                break;
            case 97:
                this.certlink = stream.readUnsignedShort();
                break;
            case 98:
                this.certtemplate = stream.readUnsignedShort();
                break;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                if (this.countobj == null) {
                    this.countobj = new int[10];
                    this.countco = new int[10];
                }

                int idx = opcode - 100;
                this.countobj[idx] = stream.readUnsignedShort();
                this.countco[idx] = stream.readUnsignedShort();
                break;
            case 110:
                this.resizeX = stream.readUnsignedShort();
                break;
            case 111:
                this.resizeY = stream.readUnsignedShort();
                break;
            case 112:
                this.resizeZ = stream.readUnsignedShort();
                break;
            case 113:
                this.ambient = stream.readByte();
                break;
            case 114:
                this.contrast = stream.readByte();
                break;
            case 115:
                this.team = stream.readUnsignedByte();
                break;
            case 121:
                this.lentLink = stream.readUnsignedShort();
                break;
            case 122:
                this.lentTemplate = stream.readUnsignedShort();
                break;
            case 125:
                this.manWearXOff = stream.readByte();
                this.manWearYOff = stream.readByte();
                this.manWearZOff = stream.readByte();
                break;
            case 126:
                this.womanWearXOff = stream.readByte();
                this.womanWearYOff = stream.readByte();
                this.womanWearZOff = stream.readByte();
                break;
            case 127:
                this.cursor1Op = stream.readUnsignedByte();
                this.cursor1 = stream.readUnsignedShort();
                break;
            case 128:
                this.cursor2Op = stream.readUnsignedByte();
                this.cursor2 = stream.readUnsignedShort();
                break;
            case 129:
                stream.readUnsignedByte();
                stream.readUnsignedShort();
                break;
            case 130:
                stream.readUnsignedByte();
                stream.readUnsignedShort();
                break;
            case 249:
                int size = stream.readUnsignedByte();

                if (this.params == null) {
                    this.params = new HashMap<>(size);
                }
                for (int i = 0; i < size; i++) {
                    boolean isString = stream.readUnsignedByte() == 1;
                    int key = (stream.readUnsignedByte() << 16) | (stream.readUnsignedByte() << 8) | stream.readUnsignedByte();
                    Object value = isString ? stream.readString() : stream.readInt();
                    this.params.put(key, value);
                }
                break;

            default:
                throw new RuntimeException("Unhandled opcode: " + opcode);
        }
    }

    public byte[] encode() {
        com.alex.io.OutputStream stream = new com.alex.io.OutputStream();

        stream.writeByte(1);
        stream.writeShort(this.model);

        if (this.name != null && !this.name.equals("null") && this.certtemplate == -1) {
            stream.writeByte(2);
            stream.writeString(this.name);
        }

        if (this.zoom2d != 2000) {
            stream.writeByte(4);
            stream.writeShort(this.zoom2d);
        }

        if (this.xAngle2D != 0) {
            stream.writeByte(5);
            stream.writeShort(this.xAngle2D);
        }

        if (this.yAngle2D != 0) {
            stream.writeByte(6);
            stream.writeShort(this.yAngle2D);
        }

        if (this.xOffset2D != 0) {
            stream.writeByte(7);
            stream.writeShort(this.xOffset2D & 0xFFFF);
        }

        if (this.yOffset2D != 0) {
            stream.writeByte(8);
            stream.writeShort(this.yOffset2D & 0xFFFF);
        }

        if (this.stackable >= 1 && this.certtemplate == -1) {
            stream.writeByte(11);
        }

        if (this.cost != 1 && this.lentTemplate == -1) {
            stream.writeByte(12);
            stream.writeInt(this.cost);
        }

        if (this.members && this.certtemplate == -1) {
            stream.writeByte(16);
        }

        if (this.manwear != -1) {
            stream.writeByte(23);
            stream.writeShort(this.manwear);
        }

        if (this.manwear2 != -1) {
            stream.writeByte(24);
            stream.writeShort(this.manwear2);
        }

        if (this.womanwear != -1) {
            stream.writeByte(25);
            stream.writeShort(this.womanwear);
        }

        if (this.womanwear2 != -1) {
            stream.writeByte(26);
            stream.writeShort(this.womanwear2);
        }

        for (int i = 0; i < 5; i++) {
            String option = this.ops[i];
            if (option == null) continue;

            stream.writeByte(30 + i);
            stream.writeString(option);
        }

        for (int i = 0; i < 5; i++) {
            String option = this.iops[i];
            if (option == null) continue;

            stream.writeByte(35 + i);
            stream.writeString(option);
        }

        if (this.recol_d != null) {
            stream.writeByte(40);
            stream.writeByte(this.recol_d.length);

            for (int i = 0; i < this.recol_d.length; i++) {
                stream.writeShort(this.recol_d[i]);
                stream.writeShort(this.recol_s[i]);
            }
        }

        if (this.retex_d != null) {
            stream.writeByte(41);
            stream.writeByte(this.retex_d.length);

            for (int i = 0; i < this.retex_d.length; i++) {
                stream.writeShort(this.retex_d[i]);
                stream.writeShort(this.retex_s[i]);
            }
        }

        if (this.recol_p != null) {
            stream.writeByte(42);
            stream.writeByte(this.recol_p.length);

            for (byte b : this.recol_p) {
                stream.writeByte(b);
            }
        }

        if (this.stockMarket) {
            stream.writeByte(65);
        }

        if (this.manwear3 != -1) {
            stream.writeByte(78);
            stream.writeShort(this.manwear3);
        }

        if (this.womanwear3 != -1) {
            stream.writeByte(79);
            stream.writeShort(this.womanwear3);
        }

        if (this.manhead != -1) {
            stream.writeByte(90);
            stream.writeShort(this.manhead);
        }

        if (this.womanhead != -1) {
            stream.writeByte(91);
            stream.writeShort(this.womanhead);
        }

        if (this.manhead2 != -1) {
            stream.writeByte(92);
            stream.writeShort(this.manhead2);
        }

        if (this.womanhead2 != -1) {
            stream.writeByte(93);
            stream.writeShort(this.womanhead2);
        }

        if (this.zAngle2D != 0) {
            stream.writeByte(95);
            stream.writeShort(this.zAngle2D);
        }

        if (this.certlink != -1) {
            stream.writeByte(97);
            stream.writeShort(this.certlink);
        }

        if (this.certtemplate != -1) {
            stream.writeByte(98);
            stream.writeShort(this.certtemplate);
        }

        if (this.countobj != null) {
            for (int i = 0; i < this.countobj.length; i++) {
                if (this.countobj[i] != 0 || this.countco[i] != 0) {
                    stream.writeByte(100 + i);
                    stream.writeShort(this.countobj[i]);
                    stream.writeShort(this.countco[i]);
                }
            }
        }

        if (this.resizeX != 128) {
            stream.writeByte(110);
            stream.writeShort(this.resizeX);
        }

        if (this.resizeY != 128) {
            stream.writeByte(111);
            stream.writeShort(this.resizeY);
        }

        if (this.resizeZ != 128) {
            stream.writeByte(112);
            stream.writeShort(this.resizeZ);
        }

        if (this.ambient != 0) {
            stream.writeByte(113);
            stream.writeByte(this.ambient);
        }

        if (this.contrast != 0) {
            stream.writeByte(114);
            stream.writeByte(this.contrast);
        }

        if (this.team != 0) {
            stream.writeByte(115);
            stream.writeByte(this.team);
        }

        if (this.lentLink != -1) {
            stream.writeByte(121);
            stream.writeShort(this.lentLink);
        }

        if (this.lentTemplate != -1) {
            stream.writeByte(122);
            stream.writeShort(this.lentTemplate);
        }

        if (this.manWearXOff != 0 || this.manWearYOff != 0 || this.manWearZOff != 0) {
            stream.writeByte(125);
            stream.writeByte(this.manWearXOff);
            stream.writeByte(this.manWearYOff);
            stream.writeByte(this.manWearZOff);
        }

        if (this.womanWearXOff != 0 || this.womanWearYOff != 0 || this.womanWearZOff != 0) {
            stream.writeByte(126);
            stream.writeByte(this.womanWearXOff);
            stream.writeByte(this.womanWearYOff);
            stream.writeByte(this.womanWearZOff);
        }

        if (this.params != null) {
            stream.writeByte(249);
            stream.writeByte(this.params.size());

            for (Object obj : this.params.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;

                int key = (Integer) entry.getKey();
                Object value = entry.getValue();

                stream.writeByte(value instanceof String ? 1 : 0);

                stream.writeByte((key >> 16) & 0xFF);
                stream.writeByte((key >> 8) & 0xFF);
                stream.writeByte(key & 0xFF);

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

   private void readOpcodeValues(InputStream stream) {
      while(true) {
         int opcode = stream.readUnsignedByte();
         if(opcode == 0) {
            return;
         }

         this.decode(stream, opcode);
      }
   }

   public void resetTextureColors() {
      this.retex_d = null;
      this.retex_s = null;
   }

   public void changeTextureColor(short originalModelColor, short modifiedModelColor) {
      if(this.retex_d != null) {
         for(int newOriginalModelColors = 0; newOriginalModelColors < this.retex_d.length; ++newOriginalModelColors) {
            if(this.retex_d[newOriginalModelColors] == originalModelColor) {
               this.retex_s[newOriginalModelColors] = modifiedModelColor;
               return;
            }
         }

         short[] var5 = Arrays.copyOf(this.retex_d, this.retex_d.length + 1);
         short[] newModifiedModelColors = Arrays.copyOf(this.retex_s, this.retex_s.length + 1);
         var5[var5.length - 1] = originalModelColor;
         newModifiedModelColors[newModifiedModelColors.length - 1] = modifiedModelColor;
         this.retex_d = var5;
         this.retex_s = newModifiedModelColors;
      } else {
         this.retex_d = new short[]{originalModelColor};
         this.retex_s = new short[]{modifiedModelColor};
      }

   }

   public void resetModelColors() {
      this.recol_d = null;
      this.recol_s = null;
   }

   public void changeModelColor(int originalModelColor, int modifiedModelColor) {
      if(this.recol_d != null) {
         for(int newOriginalModelColors = 0; newOriginalModelColors < this.recol_d.length; ++newOriginalModelColors) {
            if(this.recol_d[newOriginalModelColors] == originalModelColor) {
               this.recol_s[newOriginalModelColors] = modifiedModelColor;
               return;
            }
         }

         int[] var5 = Arrays.copyOf(this.recol_d, this.recol_d.length + 1);
         int[] newModifiedModelColors = Arrays.copyOf(this.recol_s, this.recol_s.length + 1);
         var5[var5.length - 1] = originalModelColor;
         newModifiedModelColors[newModifiedModelColors.length - 1] = modifiedModelColor;
         this.recol_d = var5;
         this.recol_s = newModifiedModelColors;
      } else {
         this.recol_d = new int[]{originalModelColor};
         this.recol_s = new int[]{modifiedModelColor};
      }

   }

   public String[] getOps() {
      return this.ops;
   }

   public String[] getIops() {
      return this.iops;
   }

   public int getEquipSlot() {
      return this.equipSlot;
   }

   public int getEquipType() {
      return this.equipType;
   }

    public boolean isLoaded() {
        return this.loaded;
    }

    public int getCost() {
        return this.cost;
    }

    public int getTeam() {
        return this.team;
    }

    public int getStackable() {
        return this.stackable;
    }

    public boolean isStockMarket() {
        return this.stockMarket;
    }

    public int getLentTemplate() {
        return this.lentTemplate;
    }

    public int getxAngle2D() {
        return this.xAngle2D;
    }

    public void setxAngle2D(int xAngle2D) {
        this.xAngle2D = xAngle2D;
    }

    public int getyAngle2D() {
        return this.yAngle2D;
    }

    public void setyAngle2D(int yAngle2D) {
        this.yAngle2D = yAngle2D;
    }

    public int getxOffset2D() {
        return this.xOffset2D;
    }

    public void setxOffset2D(int xOffset2D) {
        this.xOffset2D = xOffset2D;
    }

    public int getyOffset2D() {
        return this.yOffset2D;
    }

    public void setyOffset2D(int yOffset2D) {
        this.yOffset2D = yOffset2D;
    }

    public int getMaleEquipModelId1() {
        return this.manwear;
    }

    public void setMaleEquipModelId1(int maleEquipModelId1) {
        this.manwear = maleEquipModelId1;
    }

    public int getFemaleEquipModelId1() {
        return this.womanwear;
    }

    public void setFemaleEquipModelId1(int femaleEquipModelId1) {
        this.womanwear = femaleEquipModelId1;
    }

    public int getMaleEquipModelId2() {
        return this.manwear2;
    }

    public void setMaleEquipModelId2(int maleEquipModelId2) {
        this.manwear2 = maleEquipModelId2;
    }

    public int getFemaleEquipModelId2() {
        return this.womanwear2;
    }

    public void setFemaleEquipModelId2(int femaleEquipModelId2) {this.womanwear2 = femaleEquipModelId2;}

    public int getInvModelId() {
        return this.model;
    }

    public void setInvModelId(int modelId) {
        this.model = modelId;
    }

    public int getInvModelZoom() {
        return this.zoom2d;
    }

    public void setInvModelZoom(int modelZoom) {
        this.zoom2d = modelZoom;
    }

    public int getManwear3() {
        return this.manwear3;
    }

    public void setManwear3(int manwear3) {
        this.manwear3 = manwear3;
    }

    public int getWomanwear3() {
        return this.womanwear3;
    }

    public void setWomanwear3(int womanwear3) {
        this.womanwear3 = womanwear3;
    }

    public int[] getRecol_d() {
        return this.recol_d;
    }

    public void setRecol_d(int[] recol_d) {
        this.recol_d = recol_d;
    }

    public int[] getRecol_s() {
        return this.recol_s;
    }

    public void setRecol_s(int[] recol_s) {
        this.recol_s = recol_s;
    }

    public int[] getCountco() {
        return this.countco;
    }

    public void setCountco(int[] countco) {
        this.countco = countco;
    }

    public int[] getCountobj() {
        return this.countobj;
    }

    public void setCountobj(int[] countobj) {
        this.countobj = countobj;
    }

    public void setStackable(int stackable) {
        this.stackable = stackable;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setMembers(boolean members) {
        this.members = members;
    }

    public void setStockMarket(boolean stockMarket) {
        this.stockMarket = stockMarket;
    }

    public void setEquipSlot(int equipSlot) {
        this.equipSlot = equipSlot;
    }

    public void setEquipType(int equipType) {
        this.equipType = equipType;
    }

    public int getLentLink() {
        return this.lentLink;
    }

    public void setLentLink(int lentLink) {
        this.lentLink = lentLink;
    }

    public void setLentTemplate(int lentTemplate) {
        this.lentTemplate = lentTemplate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isWearItem() {
        return this.equipSlot != -1;
    }

    public boolean isMembers() {
        return this.members;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
      return this.id + " - " + this.name;
   }

    public boolean hasSpecialBar() {
        if (this.params == null) {
            return false;
        } else {
            Object specialBar = this.params.get(Integer.valueOf(686));
            return specialBar != null && specialBar instanceof Integer && ((Integer) specialBar).intValue() == 1;
        }
    }

    public int getRenderAnimId() {
        if (this.params == null) {
            return 1426;
        } else {
            Object animId = this.params.get(Integer.valueOf(644));
            return animId != null && animId instanceof Integer ? ((Integer) animId).intValue() : 1426;
        }
    }

    public int getQuestId() {
        if (this.params == null) {
            return -1;
        } else {
            System.out.println(this.params);
            Object questId = this.params.get(Integer.valueOf(861));
            return questId != null && questId instanceof Integer ? ((Integer) questId).intValue() : -1;
        }
    }

    public HashMap getWearingSkillRequirements() {
        if (this.params == null) {
            return null;
        } else {
            HashMap skills = new HashMap();
            int nextLevel = -1;
            int nextSkill = -1;
            Iterator var5 = this.params.keySet().iterator();

            while (var5.hasNext()) {
                int key = ((Integer) var5.next()).intValue();
                Object value = this.params.get(Integer.valueOf(key));
                if (!(value instanceof String)) {
                    if (key == 23) {
                        skills.put(Integer.valueOf(4), value);
                        skills.put(Integer.valueOf(11), Integer.valueOf(61));
                    } else if (key >= 749 && key < 797) {
                        if (key % 2 == 0) {
                            nextLevel = ((Integer) value).intValue();
                        } else {
                            nextSkill = ((Integer) value).intValue();
                        }

                        if (nextLevel != -1 && nextSkill != -1) {
                            skills.put(Integer.valueOf(nextSkill), Integer.valueOf(nextLevel));
                            nextLevel = -1;
                            nextSkill = -1;
                        }
                    }
                }
            }

            return skills;
        }
    }

    private void setDefaultOptions() {
        this.ops = new String[]{null, null, "Take", null, null};
        this.iops = new String[]{null, null, null, null, "Drop"};
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

                int size = Utils.getItemDefinitionsSize(cache);

                for (int id = 0; id < size; id++) {
                    ItemDefinition item = new ItemDefinition(cache, id, false);
                    item.loadItemDefinition(cache);

                    if (!item.loaded) {
                        continue;
                    }

                    Class<?> clazz = item.getClass();
                    Field[] fields = clazz.getDeclaredFields();

                    boolean wroteSomething = false;
                    StringBuilder buffer = new StringBuilder();

                    buffer.append("========== ITEM ").append(item.id).append(" ==========\n");

                    for (Field field : fields) {

                        if (Modifier.isStatic(field.getModifiers())) continue;
                        if (field.getName().equals("params")) continue;

                        field.setAccessible(true);

                        Object value;
                        try {
                            value = field.get(item);
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
                        if (value instanceof Object[] && ((Object[]) value).length == 0) continue;

                        String valueString;

                        if (value instanceof int[]) {
                            valueString = Arrays.toString((int[]) value);
                        } else if (value instanceof byte[]) {
                            valueString = Arrays.toString((byte[]) value);
                        } else if (value instanceof Object[]) {
                            valueString = Arrays.toString((Object[]) value);
                        } else {
                            valueString = value.toString();
                        }

                        buffer.append(field.getName())
                                .append(" = ")
                                .append(valueString)
                                .append("\n");

                        wroteSomething = true;
                    }

                    if (item.params != null && !item.params.isEmpty()) {
                        buffer.append("===== PARAMS =====\n");

                        for (Object entryObj : item.params.entrySet()) {
                            Map.Entry<Integer, Object> entry = (Map.Entry<Integer, Object>) entryObj;

                            Object v = entry.getValue();

                            if (v instanceof String) {
                                buffer.append(entry.getKey())
                                        .append(" (string) = \"")
                                        .append(v)
                                        .append("\"\n");
                            } else {
                                buffer.append(entry.getKey())
                                        .append(" (int) = ")
                                        .append(v)
                                        .append("\n");
                            }
                        }

                        wroteSomething = true;
                    }

                    if (wroteSomething) {
                        writer.println(buffer.toString());
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(Store store)
    {
        store.getIndexes()[19].putFile(this.getArchiveId(), this.getFileId(), this.encode());
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
