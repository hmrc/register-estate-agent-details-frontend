package pages

import pages.behaviours.PageBehaviours


class AgentInternalReferencePageSpec extends PageBehaviours {

  "AgentInternalReferencePage" must {

    beRetrievable[String](AgentInternalReferencePage)

    beSettable[String](AgentInternalReferencePage)

    beRemovable[String](AgentInternalReferencePage)
  }
}
