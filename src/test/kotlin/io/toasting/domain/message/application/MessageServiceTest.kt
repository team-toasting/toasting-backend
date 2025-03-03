package io.toasting.domain.message.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.toasting.creator.MessageCreator
import io.toasting.domain.member.entity.Member
import io.toasting.domain.member.entity.MemberDetails
import io.toasting.domain.member.repository.MemberRepository
import io.toasting.domain.message.applicatoin.MessageService
import io.toasting.domain.message.entity.ChatMember
import io.toasting.domain.message.entity.ChatRoom
import io.toasting.domain.message.entity.Message
import io.toasting.domain.message.repository.ChatMemberRepository
import io.toasting.domain.message.repository.ChatRoomRepository
import io.toasting.domain.message.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class MessageServiceTest private constructor() : BehaviorSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var memberRepository: MemberRepository
    @Autowired
    private lateinit var messageRepository: MessageRepository
    @Autowired
    private lateinit var messageService: MessageService
    @Autowired
    private lateinit var chatRoomRepository: ChatRoomRepository
    @Autowired
    private lateinit var chatMemberRepository: ChatMemberRepository

    init {
        afterTest {
            messageRepository.deleteAll()
            memberRepository.deleteAll()
        }

        Given("member1과 member2의 채팅방, member1과 member3의 채팅방," +
                    "member2가 메세지 10개, member3이 메시지 5개 보낸 것이 주어지고,") {
            val member1 = Member.defaultMember("member1", "member1@test.com")
            val member2 = Member.defaultMember("member2", "member2@test.com")
            val member3 = Member.defaultMember("member3", "member3@test.com")
            memberRepository.saveAll(mutableListOf(member1, member2, member3))

            val chatRoom1 = ChatRoom()
            val chatRoom2 = ChatRoom()
            chatRoomRepository.saveAll(mutableListOf(chatRoom1, chatRoom2))

            val chatMember1With2 = ChatMember(null, chatRoom1, member1.id!!)
            val chatMember2 = ChatMember(null, chatRoom1, member2.id!!)
            val chatMember1With3 = ChatMember(null, chatRoom2, member1.id!!)
            val chatMember3 = ChatMember(null, chatRoom2, member3.id!!)
            chatMemberRepository.saveAll(mutableListOf(chatMember1With2, chatMember2, chatMember1With3, chatMember3))

            val messageList: MutableList<Message> = mutableListOf()
            for (i in 0 until 10) {
                val message = MessageCreator.readMessage("read message", member2.id!!, chatRoom1)
                messageList.add(message)
            }
            for (i in 0 until 10) {
                val message = MessageCreator.unreadMessage("2->1", member2.id!!, chatRoom1)
                messageList.add(message)
            }
            for (i in 0 until 5) {
                val message = MessageCreator.unreadMessage("3->1", member3.id!!, chatRoom2)
                messageList.add(message)
            }
            messageRepository.saveAll(messageList)

            When("member1이 읽지 않은 메세지 개수를 조회했을 때") {
                val memberDetails = MemberDetails.from(member1)
                val result = messageService.getUnreadMessageCount(memberDetails);

                Then("15가 반환되어야 한다.") {
                    result.count shouldBe 15
                }
            }
        }
    }

}