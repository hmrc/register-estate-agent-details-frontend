#!/bin/bash

echo ""
echo "Applying migration AgentUKAddressYesNo"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /agentUKAddressYesNo                        controllers.AgentUKAddressYesNoController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /agentUKAddressYesNo                        controllers.AgentUKAddressYesNoController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeAgentUKAddressYesNo                  controllers.AgentUKAddressYesNoController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeAgentUKAddressYesNo                  controllers.AgentUKAddressYesNoController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "agentUKAddressYesNo.title = agentUKAddressYesNo" >> ../conf/messages.en
echo "agentUKAddressYesNo.heading = agentUKAddressYesNo" >> ../conf/messages.en
echo "agentUKAddressYesNo.checkYourAnswersLabel = agentUKAddressYesNo" >> ../conf/messages.en
echo "agentUKAddressYesNo.error.required = Select yes if agentUKAddressYesNo" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentUKAddressYesNoUserAnswersEntry: Arbitrary[(AgentUKAddressYesNoPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[AgentUKAddressYesNoPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryAgentUKAddressYesNoPage: Arbitrary[AgentUKAddressYesNoPage.type] =";\
    print "    Arbitrary(AgentUKAddressYesNoPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(AgentUKAddressYesNoPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def agentUKAddressYesNo: Option[AnswerRow] = userAnswers.get(AgentUKAddressYesNoPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"agentUKAddressYesNo.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.AgentUKAddressYesNoController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration AgentUKAddressYesNo completed"
