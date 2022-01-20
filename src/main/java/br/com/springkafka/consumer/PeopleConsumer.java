package br.com.springkafka.consumer;

import br.com.springkafka.People;
import br.com.springkafka.domain.Book;
import br.com.springkafka.repository.PeopleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class PeopleConsumer {

    private PeopleRepository peopleRepository;

    //Acknowledgment é para indicar que a mensagem foi lida! Deixamos isso manual
    @KafkaListener(topics = "${topic.name}")
    public void consumer(ConsumerRecord<String, People> mensagem, Acknowledgment ack){

        var people = mensagem.value();

        //É como se estivessemos dando new People(), mas com lombok.
        var peopleEntity = br.com.springkafka.domain.People.builder().build();

        //Motando o objeto People de Domain (Entity)
        peopleEntity.setId(people.getId().toString());
        peopleEntity.setCpf(people.getCpf().toString());
        peopleEntity.setName(people.getName().toString());

        //Pegando cada Livro em String (CharSequence) e transformando na entidade Book e depois setando no People Domain
        peopleEntity.setBooks(people.getBooks().stream()
                .map(book -> Book.builder()
                        .people(peopleEntity)
                        .name(book.toString())
                        .build()).collect(Collectors.toList()));

        peopleRepository.save(peopleEntity);

        //Confirmo que a mensagem foi lida.
        ack.acknowledge();

    }
}
