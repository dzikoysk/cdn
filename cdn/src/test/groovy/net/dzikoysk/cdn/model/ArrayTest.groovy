package net.dzikoysk.cdn.model

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class ArrayTest {

    @Test
    void 'should remove list operators' () {
        Array section = new Array([], 'list', [
                new Unit('- val1 '),
                new Unit('- " val2 "')
        ])

        assertEquals(['val1', ' val2 '], section.getList())
    }

    @Test
    void 'should return list' () {
        Section section = new Section([], 'section', [
                new Array([], 'list', [
                        new Unit('record 1'),
                        new Unit('- record 2 : with semicolon')
                ])
        ])

        assertEquals(['record 1', '- record 2 : with semicolon'], section.getList('list', [ 'default' ]))
    }


}
