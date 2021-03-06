package com.theoryinpractise.halbuilder.impl.representations;

import com.theoryinpractise.halbuilder.api.RepresentationException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.fail;

public class NamespaceManagerTest {

  @Test
  public void testNamespaceNeedsRel()
      throws Exception {

    NamespaceManager ns = NamespaceManager.EMPTY;
    try {
      ns.withNamespace("tst", "http://localhost/test/rel");
      fail("Should not allow us to add a namespace href without {rel}");
    } catch (RepresentationException e) {
      //
    }
    try {
      ns.withNamespace("tst", "http://localhost/test/{rel}");
    } catch (RepresentationException e) {
      fail("Should not fail when adding a valid namespace href");
    }
  }

  @DataProvider
  public Object[][] provideNamespaceData() {
    return new Object[][]{
        {"tst", "http://localhost/test/{rel}", "http://localhost/test/foo", "tst:foo"},
        {"tst", "http://localhost/test/{rel}/spec", "http://localhost/test/foo/spec", "tst:foo"}
    };
  }

  @Test(dataProvider = "provideNamespaceData")
  public void testCurrieHref(String ns, String href, String original, String curried)
      throws Exception {
    NamespaceManager namespaceManager = NamespaceManager.EMPTY;
    NamespaceManager updatedNamespaceManager = namespaceManager.withNamespace(ns, href);
    assertThat(namespaceManager.currieHref(original)).isEqualTo(original);
    assertThat(updatedNamespaceManager.currieHref(original)).isEqualTo(curried);
  }

  @Test(dataProvider = "provideNamespaceData")
  public void testUnCurrieHref(String ns, String href, String original, String curried)
      throws Exception {
    NamespaceManager namespaceManager = NamespaceManager.EMPTY.withNamespace(ns, href);

    assertThat(namespaceManager.resolve(curried).isRight()).isTrue();
    assertThat(namespaceManager.resolve(curried).get()).isEqualTo(original);
  }

}
