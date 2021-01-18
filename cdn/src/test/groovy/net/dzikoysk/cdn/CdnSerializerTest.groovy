package net.dzikoysk.cdn


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnSerializerTest {

    @Test
    void 'should serialize entity' () {
        assertEquals """
        # Root entry description
        rootEntry: default value
        
        // Section description
        section {
          # Random value
          subEntry: -1
          # List description
          list [
            record
            record : with : semicolons
          ]
          # Custom object
          custom {
            id: rtx
            count: 3070
          }
        }
        """.stripIndent().trim(), CDN.defaultInstance().render(new TestConfiguration())
    }

    @Test
    void 'should serialize entity with indentation based formatting' () {
        assertEquals """
        # Root entry description
        rootEntry: default value
        
        // Section description
        section:
          # Random value
          subEntry: -1
          # List description
          list:
            - record
            - record : with : semicolons
          # Custom object
          custom:
            id: rtx
            count: 3070
        """.stripIndent().trim(), CDN.configure().enableYamlLikeFormatting().build().render(new TestConfiguration())
    }

}
