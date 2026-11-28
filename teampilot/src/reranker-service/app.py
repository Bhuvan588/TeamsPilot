from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import CrossEncoder

app = FastAPI(title="TeamPilot Reranking Service")

model = CrossEncoder(
    "cross-encoder/ms-marco-MiniLM-L-6-v2"
)


class Candidate(BaseModel):
    id: str
    content: str


class RerankRequest(BaseModel):
    query: str
    candidates: list[Candidate]


class RerankResult(BaseModel):
    id: str
    score: float


@app.post("/rerank", response_model=list[RerankResult])
def rerank(request: RerankRequest):

    pairs = [
        (request.query, candidate.content)
        for candidate in request.candidates
    ]

    scores = model.predict(pairs)

    results = [
        RerankResult(
            id=candidate.id,
            score=float(score)
        )
        for candidate, score in zip(
            request.candidates, scores
        )
    ]

    return sorted(
        results,
        key=lambda result: result.score,
        reverse=True
    )


@app.get("/health")
def health():
    return {"status": "UP"}