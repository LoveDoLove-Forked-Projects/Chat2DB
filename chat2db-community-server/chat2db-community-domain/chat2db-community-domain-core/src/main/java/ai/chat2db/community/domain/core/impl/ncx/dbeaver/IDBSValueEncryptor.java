


package ai.chat2db.community.domain.core.impl.ncx.dbeaver;


public interface IDBSValueEncryptor {

    byte[] encryptValue(byte[] value);

    byte[] decryptValue(byte[] value);

}
