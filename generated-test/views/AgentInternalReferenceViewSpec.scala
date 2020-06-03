package views

import controllers.routes
import forms.AgentInternalReferenceFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.AgentInternalReferenceView

class AgentInternalReferenceViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "agentInternalReference"

  val form = new AgentInternalReferenceFormProvider()()

  "AgentInternalReferenceView view" must {

    val view = viewFor[AgentInternalReferenceView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix, routes.AgentInternalReferenceController.onSubmit(NormalMode).url)
  }
}
