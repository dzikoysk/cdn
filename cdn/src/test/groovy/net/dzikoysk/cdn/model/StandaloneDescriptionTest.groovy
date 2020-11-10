package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class StandaloneDescriptionTest {

    def description = ['', '# desc']

    @Test
    void 'should store description' () {
        def element = new StandaloneDescription(description)
        assertEquals(description, element.getValue())
        assertEquals(description, element.getDescription())
    }

}
