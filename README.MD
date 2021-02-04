

### 2021.02.04 16:02:36

- #### 签名
    mapmarks.jks
    octopus
    123321

    C:\Users\wangxiaohao>keytool -v -list -keystore D:\work\gitcode\project\mapmarks.jks
    输入密钥库口令:
    密钥库类型: jks
    密钥库提供方: SUN

    您的密钥库包含 1 个条目

    别名: octopus
    创建日期: 2021-2-4
    条目类型: PrivateKeyEntry
    证书链长度: 1
    证书[1]:
    所有者: CN=octopus, OU=octopus, O=octopus, L=octopus, ST=octopus, C=octopus
    发布者: CN=octopus, OU=octopus, O=octopus, L=octopus, ST=octopus, C=octopus
    序列号: 19ba8625
    有效期为 Thu Feb 04 16:08:12 CST 2021 至 Mon Jan 29 16:08:12 CST 2046
    证书指纹:
             MD5:  1F:F8:FD:F2:3D:23:E0:65:46:97:A7:B3:0D:32:4C:A8
             SHA1: 58:1C:3A:48:C3:2E:F0:F0:8F:2E:5A:A1:97:FA:F1:D9:8A:D5:4E:67
             SHA256: 5F:70:8E:3E:9C:3C:CC:97:6F:35:B2:EF:69:59:3B:D0:AA:D8:67:00:4F:BA:91:71:D1:24:F8:C2:6A:61:C3:21
    签名算法名称: SHA256withRSA
    主体公共密钥算法: 2048 位 RSA 密钥
    版本: 3

    扩展:

    #1: ObjectId: 2.5.29.14 Criticality=false
    SubjectKeyIdentifier [
    KeyIdentifier [
    0000: C7 36 A5 CA C5 96 DB 05   15 5A 48 26 94 DB 4F CF  .6.......ZH&..O.
    0010: 08 23 3A EA                                        .#:.
    ]
    ]



    *******************************************
    *******************************************



    Warning:
    JKS 密钥库使用专用格式。建议使用 "keytool -importkeystore -srckeystore D:\work\gitcode\project\mapmarks.jks -destkeystore D:\work\gitcode\project\mapmarks.jks -deststoretype pkcs12" 迁移到 行业标准格式 PKCS12。

- #### 高德地图key申请


2021.02.04 16:02:18

- #### 立项

  ```java
  com.octopus.mapmarks
  ```
  
  