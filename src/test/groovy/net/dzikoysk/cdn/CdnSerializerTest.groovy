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
          subEntry: 7
        }
        """.stripIndent().trim(), CDN.defaultInstance().compose(new TestConfiguration())
    }

}
