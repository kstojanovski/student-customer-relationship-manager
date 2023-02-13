@WebMvcTest is not loading (leads to not testing) the validation annotations.
@WebMvcTest should be used with mockito.
@WebMvcTest can be used with mockk but with a workaround, through a test configuration (@TestConfiguration) class.