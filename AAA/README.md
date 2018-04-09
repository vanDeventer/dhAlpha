# AAA System

The Authentication, Authorization, and Accounting (AAA) System using NGAC (Next Generation Access Control) for fine granularity has been implemented here for demonstration.
This application was used as the use-case for a security article at the [first IEEE International Conference on Industrial Cyber-Physical Systems.](https://icps2018.net)

### prerequisites for running AAA System on a computer or a  device like beagleboneblack

- Java 1.8
- My SQL server 5.7
- OpenSSL

#### *Instructions for downloading and installing the prerequisites on a computer*

**Java 1.8:** 

- Download the JRE installation compatible for your operating system from [here](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).
- Extract the package and copy the path of bin to the `PATH` environment variable.

**MySQL server 5.7:**

- Follow the installation procedure for your corresponding operating system from [here](https://dev.mysql.com/doc/refman/5.7/en/installing.html).

 **OpenSSL:**

- Follow the links below corresponding to your OS to install openSSL.
  - [Windows](https://blog.didierstevens.com/2015/03/30/howto-make-your-own-cert-with-openssl-on-windows/)
  - [Linux](https://geeksww.com/tutorials/libraries/openssl/installation/installing_openssl_on_ubuntu_linux.php)
  - [MacOS](http://macappstore.org/openssl/)

#### *Instructions for downloading and installing the prerequisites on a beagleboneblack device*

**JDKinstallation:**

1. Download **ejdk-8u161-linux-arm-sflt.tar.gz** to your computer from oracle website

2. If your computer is windows, in command prompt, enter *psftp*

3. Type open 130.240.234.49 (IPaddress of your BBB) and login as: debian and password: temppwd

4. In command prompt, change local directory using

   ​	`lcd “folderpath of .gz file”`

5.      use `mput **ejdk-8u161-linux-arm-sflt.tar.gz** ` to copies the tar file to BBB.


6. SSH to BBB from putty and give ls

7.      It displays the .gz file in the list

8.      Make a folder named java in working directoryand copy the .gz file over there and extract it

        > $mkdir java
        >
        > $mv  **ejdk-8u161-linux-arm-sflt.tar.gz** java
        >
        > $cd java
        >
        > $tar xzf **ejdk-8u161-linux-arm-sflt.tar.gz**
        >
        > $sudo nano ~/.bashrc

9.      Add lines 

        `exportPATH=$PATH:/root/java/ejdk1.8.0_161/bin`

        `exportJAVA_HOME=/root/java/ejdk1.8.0_161`

10.  In SSH window, type java –version and this should display java version 

**MySQL installation:**

1.      In SSH window, type `apt-get install mysql-server-5.5`
2.      It prompt for password and give `root`
3.      After successful installation, type `sudo service mysql status` and the status should be active.
4.      If any errors occur with installation, try to clean any traces of MySQL (<https://stackoverflow.com/questions/42305329/mysql-installation-errorunable-to-set-password-for-the-mysql-root-user>)  and start over with installation.
5.      Once installation is successful, type `sudonano /etc/mysql/my.cnf` and change *bind-address* to *0.0.0.0*
6.      Type `sudomysql –p` and give password `root`
7.      MySQL prompt appears on successful installation







