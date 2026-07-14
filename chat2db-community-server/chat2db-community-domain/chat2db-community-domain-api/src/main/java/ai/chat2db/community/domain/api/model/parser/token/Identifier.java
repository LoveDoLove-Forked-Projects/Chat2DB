package ai.chat2db.community.domain.api.model.parser.token;


import ai.chat2db.community.domain.api.model.parser.node.SimpleNode;
import org.antlr.v4.runtime.Token;


public class Identifier extends SimpleNode {
    private String identifierName;
    private String identifierAlias;
    private String identifierType;
    private String identifierFunctionName;

    private String identifierDatabase;
    private String identifierSchema;
    private String identifierTable;
    private String identifierDataType;
    private String identifierComment;

    public Identifier() {
    }

    public String getIdentifierDataType() {
        return identifierDataType;
    }

    public void setIdentifierDataType(String identifierDataType) {
        this.identifierDataType = identifierDataType;
    }

    public String getIdentifierComment() {
        return identifierComment;
    }

    public void setIdentifierComment(String identifierComment) {
        this.identifierComment = identifierComment;
    }

    public String getIdentifierDatabase() {
        return identifierDatabase;
    }

    public void setIdentifierDatabase(String identifierDatabase) {
        this.identifierDatabase = identifierDatabase;
    }

    public String getIdentifierSchema() {
        return identifierSchema;
    }

    public void setIdentifierSchema(String identifierSchema) {
        this.identifierSchema = identifierSchema;
    }

    public String getIdentifierTable() {
        return identifierTable;
    }

    public void setIdentifierTable(String identifierTable) {
        this.identifierTable = identifierTable;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    public String getIdentifierAlias() {
        return identifierAlias;
    }

    public void setIdentifierAlias(String identifierAlias) {
        this.identifierAlias = identifierAlias;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append("Identifier{");
        stringBuilder.append("identifierDatabase= ").append(identifierDatabase).append('\n');
        stringBuilder.append("identifierSchema= ").append(identifierSchema).append('\n');
        stringBuilder.append("identifierTable= ").append(identifierTable).append('\n');
        stringBuilder.append("identifierName= ").append(identifierName).append('\n');
        stringBuilder.append(", identifierAlias= ").append(identifierAlias).append('\n');
        stringBuilder.append(", identifierType= ").append(identifierType).append('\n');
        stringBuilder.append(", identifierDataType= ").append(identifierDataType).append('\n');
        stringBuilder.append(", identifierComment= ").append(identifierComment).append('\n');
        stringBuilder.append("firstToken= ").append(firstToken).append('\n');
        stringBuilder.append("lastToken= ").append(lastToken).append('\n');
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public Identifier(String text, String identifierName, Token firstToken) {
        super(firstToken);
        this.identifierName = text;
        this.identifierType = identifierName;
    }

    public Identifier(String identifierName, String identifierType, String identifierAlias, Token firstToken, Token lastToken) {
        super(firstToken, lastToken);
        this.identifierName = identifierName;
        this.identifierAlias = identifierAlias;
        this.identifierType = identifierType;

    }


}
