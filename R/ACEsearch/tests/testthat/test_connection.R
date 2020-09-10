library(testthat)

test_that("malformend_connection_string", {
  expect_error(ACEsearch::ACEsearch.connect("xxx"), "Could not connect to ACE instance");
})
