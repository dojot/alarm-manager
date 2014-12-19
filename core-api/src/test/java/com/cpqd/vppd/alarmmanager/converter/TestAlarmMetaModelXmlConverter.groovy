package com.cpqd.vppd.alarmmanager.converter

import com.cpqd.vppd.alarmmanager.core.converter.AlarmMetaModelXmlConverter
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField
import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * Created by fabio on 10/12/14.
 */
class TestAlarmMetaModelXmlConverter extends Specification {
    private AlarmMetaModelXmlConverter alarmMetaModelXmlConverter

    def setup() {
        alarmMetaModelXmlConverter = new AlarmMetaModelXmlConverter()
    }

    def "convert an alarm metamodel to XML"() {
        given: "there is an AlarmMetaModel instance"
        AlarmMetaModel metaModel = new AlarmMetaModel()
        metaModel.domain = "testDomain"
        metaModel.namespace = "testNamespace"
        metaModel.addPrimarySubjectField(new DomainSpecificField("field1", "alphanumeric"))
        metaModel.addAdditionalDataField(new DomainSpecificField("field1", "numeric"))

        when: "the instance is converted to an XML string"
        String xml = alarmMetaModelXmlConverter.toXml(metaModel)

        then: "the generated XML string contains all the instance data as expected"
        xml == "<alarm-metamodel><domain>testDomain</domain><namespace>testNamespace</namespace><primary-subject>" +
                "<primary-field type=\"alphanumeric\">field1</primary-field></primary-subject><additional-data>" +
                "<additional-field type=\"numeric\">field1</additional-field></additional-data></alarm-metamodel>"
    }

    def "convert a metamodel XML into an instance"() {
        given: "there is an XML which represents a metamodel"
        String xml = FileUtils.readFileToString(new File("src/test/resources/xml/metamodel.xml"))

        when: "the XML string is converted to a metamodel instance"
        AlarmMetaModel metaModel = alarmMetaModelXmlConverter.fromXml(xml)

        then: "a metamodel instance should be instantiated with the following properties"
        metaModel != null
        metaModel.domain == "testDomain"
        metaModel.namespace == "testNamespace"
        metaModel.primarySubject.size() == 1
        metaModel.primarySubject.getAt(0).name == "field1"
        metaModel.primarySubject.getAt(0).type == "alphanumeric"
        metaModel.additionalData.size() == 1
        metaModel.additionalData.getAt(0).name == "field1"
        metaModel.additionalData.getAt(0).type == "numeric"
    }
}
