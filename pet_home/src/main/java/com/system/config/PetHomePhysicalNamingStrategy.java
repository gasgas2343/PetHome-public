package com.system.config;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class PetHomePhysicalNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        if (logicalName != null && logicalName.getText().startsWith("Wash")) {
            return Identifier.toIdentifier(logicalName.getText(), logicalName.isQuoted());
        }
        return super.toPhysicalTableName(logicalName, jdbcEnvironment);
    }
}
