package com.theoryinpractise.halbuilder.impl.bytecode;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.TreeMap;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Comparator.naturalOrder;

public class InterfaceRendererTest {

  @Test
  public void testRendering() {

    RepresentationFactory representationFactory = new DefaultRepresentationFactory();
    Map<String, Object> properties = TreeMap.<String, Object>empty(naturalOrder())
        .put("name", "Joe Smith")
        .put("id", 1)
        .put("expired", false)
        .put("age", 40);

    List<Link> links = List.of(new Link("self", "/123/456"));

    final List<Representation> representations = List.of(representationFactory.newRepresentation());

    TreeMap<String, List<? extends ReadableRepresentation>> embedded = TreeMap.empty(naturalOrder());
    embedded = embedded.put("user", representations);

    InterfaceRenderer<IPerson> renderer = InterfaceRenderer.newInterfaceRenderer(IPerson.class);

    IPerson person = renderer.render(properties, links, embedded);
    assertFactsAboutPerson(person);

    IPerson person2 = representationFactory.newRepresentation("/123/456")
                                           .withProperties(properties)
                                           .toClass(IPerson.class);

    assertFactsAboutPerson(person2);

  }

  public void assertFactsAboutPerson(IPerson person) {
    assertThat(person).isNotNull();
    assertThat(person.getName()).isEqualTo("Joe Smith");
    assertThat(person.getAge()).isEqualTo(40);
    assertThat(person.getLinks()).isNotEmpty();
  }

  public interface IPerson {
    Integer getAge();

    Boolean getExpired();

    Integer getId();

    String getName();

    List<Link> getLinks();

    Map<String, Collection<ReadableRepresentation>> getEmbedded();
  }

}
