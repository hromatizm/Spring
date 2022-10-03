package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.BookDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;

@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/book",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
    private final BookDataFacade bookDataFacade;

    public BookController(BookDataFacade bookDataFacade) {
        this.bookDataFacade = bookDataFacade;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create new book.",
            responses = {
                    @ApiResponse(description = "Book id with details",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse createBook(
            @RequestBody BookRequest request,
            @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId
    ) {
        BookResponse response = bookDataFacade.createBook(request);
        log.info("Response with created book: {}", response);
        return response;
    }

    @PutMapping(value = "/update/{bookId}")
    @Operation(summary = "Update book details.",
            responses = {
                    @ApiResponse(description = "Book's details",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse updateBook(
            @RequestBody BookRequest request,
            @PathVariable Long bookId
    ) {
        BookResponse response = bookDataFacade.updateBook(request, bookId);
        log.info("Response with updated book: {}", response);
        return response;
    }

    @GetMapping(value = "/get/{bookId}")
    @Operation(summary = "Get book.",
            responses = {
                    @ApiResponse(description = "Book's details",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse getBook(@PathVariable Long bookId) {
        BookResponse response = bookDataFacade.getBookById(bookId);
        log.info("Response with book: {}", response);
        return response;
    }

    @DeleteMapping(value = "/delete/{bookId}")
    @Operation(summary = "Delete book.",
            responses = {@ApiResponse(description = "Response status line")})
    public void deleteBook(@PathVariable Long bookId) {
        log.info("Delete books:  bookId {}", bookId);
        bookDataFacade.deleteBook(bookId);
    }
}
